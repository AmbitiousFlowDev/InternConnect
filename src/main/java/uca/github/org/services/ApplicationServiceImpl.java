package uca.github.org.services;

import jakarta.persistence.EntityNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import uca.github.org.models.Application;
import uca.github.org.models.Internship;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.repositories.InternshipRepository;
import uca.github.org.models.Notification;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final NotificationService notificationService;

    @Override
    public List<Application> getUserApplications(User user) {
        return applicationRepository.findByApplicantOrderBySubmittedAtDesc(user);
    }

    @Override
    public List<Application> getUserApplicationsByStatus(
            User user,
            Application.ApplicationStatus status
    ) {
        return applicationRepository
                .findByApplicantAndStatusOrderBySubmittedAtDesc(user, status);
    }

    @Override
    @Transactional
    public Application updateApplicationStatus(
            Long applicationId,
            Application.ApplicationStatus newStatus
    ) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Application not found: " + applicationId
                        )
                );

        application.setStatus(newStatus);

        Application savedApplication = applicationRepository.save(application);

        notificationService.createNotification(
                savedApplication.getApplicant(),
                Notification.NotificationType.STATUS_UPDATE,
                "Statut de candidature mis à jour",
                "Votre candidature est maintenant : " + newStatus,
                "/applications/status"
        );

        return savedApplication;
    }

    @Override
    public Map<Application.ApplicationStatus, Long> getStatusSummary(User user) {
        Map<Application.ApplicationStatus, Long> summary = new LinkedHashMap<>();

        for (Application.ApplicationStatus status : Application.ApplicationStatus.values()) {
            summary.put(
                    status,
                    applicationRepository.countByApplicantAndStatus(user, status)
            );
        }

        return summary;
    }

    @Override
    @Transactional
    public Application apply(
            User applicant,
            Long internshipId,
            String coverLetter
    ) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Internship not found: " + internshipId
                        )
                );

        boolean alreadyApplied = applicationRepository.existsByApplicantAndInternship(
                applicant,
                internship
        );

        if (alreadyApplied) {
            throw new IllegalStateException(
                    "Vous avez déjà postulé à cette offre."
            );
        }

        Application application = Application.builder()
                .applicant(applicant)
                .internship(internship)
                .coverLetter(coverLetter)
                .status(Application.ApplicationStatus.SUBMITTED)
                .build();

        Application savedApplication = applicationRepository.save(application);

        notificationService.createNotification(
                internship.getPoster(),
                Notification.NotificationType.APPLICATION,
                "Nouvelle candidature",
                applicant.getFirstName() + " " + applicant.getLastName()
                        + " a postulé à votre offre : " + internship.getTitle(),
                "/applications/offers/" + internship.getId()
        );

        return savedApplication;
    }

    @Override
    @Transactional
    public void withdraw(
            User applicant,
            Long applicationId
    ) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Application not found: " + applicationId
                        )
                );

        if (application.getApplicant() == null
                || application.getApplicant().getId() == null
                || !application.getApplicant().getId().equals(applicant.getId())) {

            throw new AccessDeniedException(
                    "You are not the owner of this application."
            );
        }

        if (application.getStatus() == Application.ApplicationStatus.ACCEPTED
                || application.getStatus() == Application.ApplicationStatus.REJECTED) {

            throw new IllegalStateException(
                    "Cannot withdraw this application."
            );
        }

        application.setStatus(Application.ApplicationStatus.WITHDRAWN);

        applicationRepository.save(application);
    }

    @Override
    public boolean hasAlreadyApplied(
            User applicant,
            Long internshipId
    ) {
        return internshipRepository.findById(internshipId)
                .map(internship ->
                        applicationRepository.existsByApplicantAndInternship(
                                applicant,
                                internship
                        )
                )
                .orElse(false);
    }

    @Override
    public List<Application> getOfferApplications(
            Long offerId,
            User currentUser,
            String applicantName,
            LocalDate submittedDate
    ) {
        Internship internship = internshipRepository.findById(offerId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Offer not found: " + offerId
                        )
                );

        if (!canViewOfferApplications(internship, currentUser)) {
            throw new IllegalArgumentException(
                    "Vous n'avez pas le droit de consulter ces candidatures."
            );
        }

        String normalizedName = normalize(applicantName);

        return applicationRepository.findByInternshipOrderBySubmittedAtDesc(internship)
                .stream()
                .filter(application -> matchesApplicantName(application, normalizedName))
                .filter(application -> matchesSubmittedDate(application, submittedDate))
                .toList();
    }

    private boolean canViewOfferApplications(
            Internship internship,
            User currentUser
    ) {
        if (currentUser == null) {
            return false;
        }

        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }

        return internship.getPoster() != null
                && internship.getPoster().getId() != null
                && internship.getPoster().getId().equals(currentUser.getId());
    }

    private boolean matchesApplicantName(
            Application application,
            String applicantName
    ) {
        if (applicantName.isBlank()) {
            return true;
        }

        User applicant = application.getApplicant();

        if (applicant == null) {
            return false;
        }

        String firstName = applicant.getFirstName() == null
                ? ""
                : applicant.getFirstName();

        String lastName = applicant.getLastName() == null
                ? ""
                : applicant.getLastName();

        String fullName = normalize(firstName + " " + lastName);
        String reversedName = normalize(lastName + " " + firstName);

        return fullName.contains(applicantName)
                || reversedName.contains(applicantName);
    }

    private boolean matchesSubmittedDate(
            Application application,
            LocalDate submittedDate
    ) {
        return submittedDate == null
                || application.getSubmittedAt() != null
                && application.getSubmittedAt().toLocalDate().equals(submittedDate);
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toLowerCase(Locale.ROOT);
    }
}
