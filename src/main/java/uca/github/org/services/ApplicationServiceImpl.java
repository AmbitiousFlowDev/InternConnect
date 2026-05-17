package uca.github.org.services;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uca.github.org.models.Application;
import uca.github.org.models.Internship;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.repositories.InternshipRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.* ;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;

    @Override
    public List<Application> getUserApplications(User user) {
        return applicationRepository
                .findByApplicantOrderBySubmittedAtDesc(user);
    }

    @Override
    public List<Application> getUserApplicationsByStatus(
            User user, Application.ApplicationStatus status) {
        return applicationRepository
                .findByApplicantAndStatusOrderBySubmittedAtDesc(user, status);
    }
    @Override
    @Transactional
    public Application updateApplicationStatus(
            Long applicationId, Application.ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Application not found: " + applicationId));
        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }

    @Override
    public Map<Application.ApplicationStatus, Long> getStatusSummary(User user) {
        Map<Application.ApplicationStatus, Long> summary = new LinkedHashMap<>();
        for (Application.ApplicationStatus status : Application.ApplicationStatus.values()) {
            summary.put(status, applicationRepository.countByApplicantAndStatus(user, status));
        }
        return summary;
    }

    @Override
    public List<Application> getOfferApplications(
            Long offerId,
            User currentUser,
            String applicantName,
            LocalDate submittedDate) {

        Internship internship = internshipRepository.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found: " + offerId));

        if (!canViewOfferApplications(internship, currentUser)) {
            throw new IllegalArgumentException("Vous n'avez pas le droit de consulter ces candidatures.");
        }

        String normalizedName = normalize(applicantName);

        return applicationRepository.findByInternshipOrderBySubmittedAtDesc(internship).stream()
                .filter(application -> matchesApplicantName(application, normalizedName))
                .filter(application -> matchesSubmittedDate(application, submittedDate))
                .toList();
    }

    private boolean canViewOfferApplications(Internship internship, User currentUser) {
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

    private boolean matchesApplicantName(Application application, String applicantName) {
        if (applicantName.isBlank()) {
            return true;
        }

        User applicant = application.getApplicant();
        if (applicant == null) {
            return false;
        }

        String fullName = normalize(applicant.getFirstName() + " " + applicant.getLastName());
        String reversedName = normalize(applicant.getLastName() + " " + applicant.getFirstName());
        return fullName.contains(applicantName) || reversedName.contains(applicantName);
    }

    private boolean matchesSubmittedDate(Application application, LocalDate submittedDate) {
        return submittedDate == null
                || application.getSubmittedAt() != null
                && application.getSubmittedAt().toLocalDate().equals(submittedDate);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
