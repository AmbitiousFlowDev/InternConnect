package uca.github.org.services;

import uca.github.org.models.Application;
import uca.github.org.models.User;

import java.util.List;
import java.util.Map;

public interface ApplicationService {

    List<Application> getUserApplications(User user);

    List<Application> getUserApplicationsByStatus(
            User user,
            Application.ApplicationStatus status
    );

    Application updateApplicationStatus(
            Long applicationId,
            Application.ApplicationStatus newStatus
    );

    Map<Application.ApplicationStatus, Long> getStatusSummary(User user);

    // POSTULER
    Application apply(
            User applicant,
            Long internshipId,
            String coverLetter
    );

    // RETIRER CANDIDATURE
    void withdraw(
            User applicant,
            Long applicationId
    );

    // VERIFIER SI DEJA POSTULE
    boolean hasAlreadyApplied(
            User applicant,
            Long internshipId
    );
}