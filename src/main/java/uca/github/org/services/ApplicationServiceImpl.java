package uca.github.org.services;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uca.github.org.models.Application;
import uca.github.org.models.Internship;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;

import java.util.* ;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

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
    // NOUVEAU
    @Override
    @Transactional
    public Application apply(User applicant, Long internshipId, String coverLetter) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found: " + internshipId));

        if (applicationRepository.existsByApplicantAndInternship(applicant, internship)) {
            throw new IllegalStateException("You have already applied to this internship.");
        }

        Application application = Application.builder()
                .applicant(applicant)
                .internship(internship)
                .coverLetter(coverLetter)
                .status(Application.ApplicationStatus.SUBMITTED)
                .build();

        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void withdraw(User applicant, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found: " + applicationId));

        if (!application.getApplicant().getId().equals(applicant.getId())) {
            throw new AccessDeniedException("You are not the owner of this application.");
        }

        if (application.getStatus() == Application.ApplicationStatus.ACCEPTED ||
            application.getStatus() == Application.ApplicationStatus.REJECTED) {
            throw new IllegalStateException("Cannot withdraw an application that is already " + application.getStatus());
        }

        application.setStatus(Application.ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
    }

    @Override
    public boolean hasAlreadyApplied(User applicant, Long internshipId) {
        return internshipRepository.findById(internshipId)
                .map(internship -> applicationRepository.existsByApplicantAndInternship(applicant, internship))
                .orElse(false);
    }
}