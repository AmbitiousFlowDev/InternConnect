package uca.github.org;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.services.ApplicationServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
    }

    // -------------------------------------------------------
    // getUserApplications
    // -------------------------------------------------------

    @Test
    void getUserApplications_shouldReturnAllApplicationsForUser() {
        // Given
        Application app1 = new Application();
        Application app2 = new Application();
        when(applicationRepository.findByApplicantOrderBySubmittedAtDesc(mockUser))
                .thenReturn(List.of(app1, app2));

        // When
        List<Application> result = applicationService.getUserApplications(mockUser);

        // Then
        assertThat(result).hasSize(2);
        verify(applicationRepository, times(1))
                .findByApplicantOrderBySubmittedAtDesc(mockUser);
    }

    @Test
    void getUserApplications_shouldReturnEmptyListWhenNone() {
        // Given
        when(applicationRepository.findByApplicantOrderBySubmittedAtDesc(mockUser))
                .thenReturn(List.of());

        // When
        List<Application> result = applicationService.getUserApplications(mockUser);

        // Then
        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------
    // getUserApplicationsByStatus
    // -------------------------------------------------------

    @Test
    void getUserApplicationsByStatus_shouldReturnFilteredApplications() {
        // Given — SUBMITTED remplace PENDING
        Application submittedApp = new Application();
        submittedApp.setStatus(Application.ApplicationStatus.SUBMITTED);

        when(applicationRepository.findByApplicantAndStatusOrderBySubmittedAtDesc(
                mockUser, Application.ApplicationStatus.SUBMITTED))
                .thenReturn(List.of(submittedApp));

        // When
        List<Application> result = applicationService.getUserApplicationsByStatus(
                mockUser, Application.ApplicationStatus.SUBMITTED);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus())
                .isEqualTo(Application.ApplicationStatus.SUBMITTED);
    }

    @Test
    void getUserApplicationsByStatus_shouldFilterByUnderReview() {
        // Given — UNDER_REVIEW remplace REVIEWED
        Application reviewApp = new Application();
        reviewApp.setStatus(Application.ApplicationStatus.UNDER_REVIEW);

        when(applicationRepository.findByApplicantAndStatusOrderBySubmittedAtDesc(
                mockUser, Application.ApplicationStatus.UNDER_REVIEW))
                .thenReturn(List.of(reviewApp));

        // When
        List<Application> result = applicationService.getUserApplicationsByStatus(
                mockUser, Application.ApplicationStatus.UNDER_REVIEW);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus())
                .isEqualTo(Application.ApplicationStatus.UNDER_REVIEW);
    }

    @Test
    void getUserApplicationsByStatus_shouldReturnEmptyWhenNoMatch() {
        // Given
        when(applicationRepository.findByApplicantAndStatusOrderBySubmittedAtDesc(
                mockUser, Application.ApplicationStatus.ACCEPTED))
                .thenReturn(List.of());

        // When
        List<Application> result = applicationService.getUserApplicationsByStatus(
                mockUser, Application.ApplicationStatus.ACCEPTED);

        // Then
        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------
    // updateApplicationStatus
    // -------------------------------------------------------

    @Test
    void updateApplicationStatus_shouldUpdateSuccessfully() {
        // Given
        Application app = new Application();
        app.setStatus(Application.ApplicationStatus.SUBMITTED);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(app)).thenReturn(app);

        // When
        Application result = applicationService.updateApplicationStatus(
                1L, Application.ApplicationStatus.UNDER_REVIEW);

        // Then
        assertThat(result.getStatus())
                .isEqualTo(Application.ApplicationStatus.UNDER_REVIEW);
        verify(applicationRepository).save(app);
    }

    @Test
    void updateApplicationStatus_shouldThrowWhenNotFound() {
        // Given
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() ->
                applicationService.updateApplicationStatus(
                        99L, Application.ApplicationStatus.ACCEPTED))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // -------------------------------------------------------
    // getStatusSummary
    // -------------------------------------------------------

    @Test
    void getStatusSummary_shouldReturnCountForAllStatuses() {
        // Given
        for (Application.ApplicationStatus status : Application.ApplicationStatus.values()) {
            when(applicationRepository.countByApplicantAndStatus(mockUser, status))
                    .thenReturn(0L);
        }
        when(applicationRepository.countByApplicantAndStatus(
                mockUser, Application.ApplicationStatus.SUBMITTED)).thenReturn(3L);
        when(applicationRepository.countByApplicantAndStatus(
                mockUser, Application.ApplicationStatus.ACCEPTED)).thenReturn(1L);

        // When
        Map<Application.ApplicationStatus, Long> summary =
                applicationService.getStatusSummary(mockUser);

        // Then
        assertThat(summary).hasSize(Application.ApplicationStatus.values().length);
        assertThat(summary).containsKey(Application.ApplicationStatus.SUBMITTED);
        assertThat(summary).containsKey(Application.ApplicationStatus.UNDER_REVIEW);
        assertThat(summary.get(Application.ApplicationStatus.SUBMITTED)).isEqualTo(3L);
        assertThat(summary.get(Application.ApplicationStatus.ACCEPTED)).isEqualTo(1L);
    }
}