package uca.github.org;

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

import static org.assertj.core.api.Assertions.assertThat;
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
    void getUserApplicationsByStatus_shouldReturnFilteredApplications() {
        // Given
        Application pendingApp = new Application();
        pendingApp.setStatus(Application.ApplicationStatus.PENDING);

        when(applicationRepository.findByApplicantAndStatusOrderBySubmittedAtDesc(
                mockUser, Application.ApplicationStatus.PENDING))
                .thenReturn(List.of(pendingApp));

        // When
        List<Application> result = applicationService.getUserApplicationsByStatus(
                mockUser, Application.ApplicationStatus.PENDING);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus())
                .isEqualTo(Application.ApplicationStatus.PENDING);
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
}