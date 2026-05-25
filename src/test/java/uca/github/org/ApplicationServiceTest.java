package uca.github.org;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uca.github.org.models.Application;
import uca.github.org.models.Internship;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.repositories.InternshipRepository;
import uca.github.org.services.AccessControlService;
import uca.github.org.services.ApplicationServiceImpl;
import uca.github.org.services.NotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private InternshipRepository internshipRepository;
    
    @Mock
    private NotificationService notificationService;

    @Mock
    private AccessControlService accessControlService;

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

    @Test
    void updateApplicationStatus_shouldRejectPosterWhoDoesNotOwnOffer() {
        User owner = User.builder().id(1L).role(User.Role.POSTER).build();
        User otherPoster = User.builder().id(2L).role(User.Role.POSTER).build();
        Internship internship = Internship.builder().id(10L).poster(owner).build();
        Application app = Application.builder()
                .internship(internship)
                .status(Application.ApplicationStatus.SUBMITTED)
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(accessControlService.canManageAnyOffer(otherPoster)).thenReturn(false);
        when(accessControlService.canViewOfferApplications(otherPoster)).thenReturn(true);

        assertThatThrownBy(() -> applicationService.updateApplicationStatus(
                1L,
                Application.ApplicationStatus.ACCEPTED,
                otherPoster))
                .hasMessageContaining("droit");

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void apply_shouldRejectPosterRole() {
        User poster = User.builder().id(1L).role(User.Role.POSTER).build();
        Internship internship = Internship.builder().id(10L).build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(accessControlService.canApplyToOffers(poster)).thenReturn(false);

        assertThatThrownBy(() -> applicationService.apply(poster, 10L, "Lettre"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("recruteurs");

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void apply_shouldAllowStudentRole() {
        User student = User.builder().id(1L).role(User.Role.USER).firstName("Sara").lastName("Amrani").build();
        User poster = User.builder().id(2L).role(User.Role.POSTER).build();
        Internship internship = Internship.builder().id(10L).poster(poster).title("Stage Java").build();
        Application saved = Application.builder().id(99L).applicant(student).internship(internship).build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(accessControlService.canApplyToOffers(student)).thenReturn(true);
        when(applicationRepository.existsByApplicantAndInternship(student, internship)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(saved);

        Application result = applicationService.apply(student, 10L, "Lettre");

        assertThat(result).isSameAs(saved);
        verify(applicationRepository).save(any(Application.class));
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

    // -------------------------------------------------------
    // getOfferApplications
    // -------------------------------------------------------

    @Test
    void getOfferApplications_shouldReturnApplicantsForOfferOwner() {
        User poster = User.builder().id(1L).role(User.Role.POSTER).build();
        Internship internship = Internship.builder().id(10L).poster(poster).build();
        Application firstApplication = Application.builder()
                .applicant(User.builder().firstName("Sara").lastName("Amrani").build())
                .internship(internship)
                .submittedAt(LocalDateTime.of(2026, 5, 10, 9, 30))
                .build();
        Application secondApplication = Application.builder()
                .applicant(User.builder().firstName("Youssef").lastName("Bennani").build())
                .internship(internship)
                .submittedAt(LocalDateTime.of(2026, 5, 11, 14, 0))
                .build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(accessControlService.canManageAnyOffer(poster)).thenReturn(false);
        when(accessControlService.canViewOfferApplications(poster)).thenReturn(true);
        when(applicationRepository.findByInternshipOrderBySubmittedAtDesc(internship))
                .thenReturn(List.of(secondApplication, firstApplication));

        List<Application> result = applicationService.getOfferApplications(10L, poster, null, null);

        assertThat(result).containsExactly(secondApplication, firstApplication);
        verify(applicationRepository).findByInternshipOrderBySubmittedAtDesc(internship);
    }

    @Test
    void getOfferApplications_shouldFilterApplicantsByName() {
        User poster = User.builder().id(1L).role(User.Role.POSTER).build();
        Internship internship = Internship.builder().id(10L).poster(poster).build();
        Application matchingApplication = Application.builder()
                .applicant(User.builder().firstName("Sara").lastName("Amrani").build())
                .internship(internship)
                .submittedAt(LocalDateTime.of(2026, 5, 10, 9, 30))
                .build();
        Application otherApplication = Application.builder()
                .applicant(User.builder().firstName("Youssef").lastName("Bennani").build())
                .internship(internship)
                .submittedAt(LocalDateTime.of(2026, 5, 11, 14, 0))
                .build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(accessControlService.canManageAnyOffer(poster)).thenReturn(false);
        when(accessControlService.canViewOfferApplications(poster)).thenReturn(true);
        when(applicationRepository.findByInternshipOrderBySubmittedAtDesc(internship))
                .thenReturn(List.of(otherApplication, matchingApplication));

        List<Application> result = applicationService.getOfferApplications(10L, poster, "sara", null);

        assertThat(result).containsExactly(matchingApplication);
    }

    @Test
    void getOfferApplications_shouldFilterApplicantsBySubmittedDate() {
        User poster = User.builder().id(1L).role(User.Role.POSTER).build();
        Internship internship = Internship.builder().id(10L).poster(poster).build();
        Application matchingApplication = Application.builder()
                .applicant(User.builder().firstName("Sara").lastName("Amrani").build())
                .internship(internship)
                .submittedAt(LocalDateTime.of(2026, 5, 10, 9, 30))
                .build();
        Application otherApplication = Application.builder()
                .applicant(User.builder().firstName("Youssef").lastName("Bennani").build())
                .internship(internship)
                .submittedAt(LocalDateTime.of(2026, 5, 11, 14, 0))
                .build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(accessControlService.canManageAnyOffer(poster)).thenReturn(false);
        when(accessControlService.canViewOfferApplications(poster)).thenReturn(true);
        when(applicationRepository.findByInternshipOrderBySubmittedAtDesc(internship))
                .thenReturn(List.of(otherApplication, matchingApplication));

        List<Application> result = applicationService.getOfferApplications(
                10L,
                poster,
                null,
                LocalDate.of(2026, 5, 10));

        assertThat(result).containsExactly(matchingApplication);
    }

    @Test
    void getOfferApplications_shouldAllowAdminToViewAnyOffer() {
        User poster = User.builder().id(1L).role(User.Role.POSTER).build();
        User admin = User.builder().id(2L).role(User.Role.ADMIN).build();
        Internship internship = Internship.builder().id(10L).poster(poster).build();
        Application application = Application.builder()
                .applicant(User.builder().firstName("Sara").lastName("Amrani").build())
                .internship(internship)
                .submittedAt(LocalDateTime.of(2026, 5, 10, 9, 30))
                .build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(accessControlService.canManageAnyOffer(admin)).thenReturn(true);
        when(applicationRepository.findByInternshipOrderBySubmittedAtDesc(internship))
                .thenReturn(List.of(application));

        List<Application> result = applicationService.getOfferApplications(10L, admin, null, null);

        assertThat(result).containsExactly(application);
    }

    @Test
    void getOfferApplications_shouldRejectUserWhoDoesNotOwnOffer() {
        User poster = User.builder().id(1L).role(User.Role.POSTER).build();
        User otherUser = User.builder().id(2L).role(User.Role.USER).build();
        Internship internship = Internship.builder().id(10L).poster(poster).build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(internship));
        when(accessControlService.canManageAnyOffer(otherUser)).thenReturn(false);
        when(accessControlService.canViewOfferApplications(otherUser)).thenReturn(false);

        assertThatThrownBy(() -> applicationService.getOfferApplications(10L, otherUser, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("droit");

        verify(applicationRepository, never()).findByInternshipOrderBySubmittedAtDesc(any());
    }
}
