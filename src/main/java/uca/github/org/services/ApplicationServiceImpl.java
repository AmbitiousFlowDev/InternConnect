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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;

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

        return applicationRepository.save(application);
    }

    @Override
    public Map<Application.ApplicationStatus, Long> getStatusSummary(User user) {

        Map<Application.ApplicationStatus, Long> summary =
                new LinkedHashMap<>();

        for (Application.ApplicationStatus status :
                Application.ApplicationStatus.values()) {

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

        System.out.println("===== APPLY DEBUG =====");
        System.out.println("Applicant ID : " + applicant.getId());
        System.out.println("Internship ID : " + internshipId);
        System.out.println("Cover Letter : " + coverLetter);
        System.out.println("=======================");

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Internship not found: " + internshipId
                        )
                );

        boolean alreadyApplied =
                applicationRepository.existsByApplicantAndInternship(
                        applicant,
                        internship
                );

        System.out.println("Already applied : " + alreadyApplied);

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

        Application savedApplication =
                applicationRepository.save(application);

        System.out.println("APPLICATION SAVED SUCCESSFULLY");
        System.out.println("Saved ID : " + savedApplication.getId());

        return savedApplication;
    }

    @Override
    @Transactional
    public void withdraw(User applicant, Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Application not found: " + applicationId
                        )
                );

        if (!application.getApplicant().getId()
                .equals(applicant.getId())) {

            throw new AccessDeniedException(
                    "You are not the owner of this application."
            );
        }

        if (application.getStatus() ==
                Application.ApplicationStatus.ACCEPTED
                ||
                application.getStatus() ==
                        Application.ApplicationStatus.REJECTED) {

            throw new IllegalStateException(
                    "Cannot withdraw this application."
            );
        }

        application.setStatus(
                Application.ApplicationStatus.WITHDRAWN
        );

        applicationRepository.save(application);
    }

    @Override
    public boolean hasAlreadyApplied(
            User applicant,
            Long internshipId
    ) {

        return internshipRepository.findById(internshipId)
                .map(internship ->
                        applicationRepository
                                .existsByApplicantAndInternship(
                                        applicant,
                                        internship
                                )
                )
                .orElse(false);
    }
}