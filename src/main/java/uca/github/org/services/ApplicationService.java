package uca.github.org.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import uca.github.org.models.Application;
import uca.github.org.models.User;

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

    Application apply(
            User applicant,
            Long internshipId,
            String coverLetter
    );

    void withdraw(
            User applicant,
            Long applicationId
    );

    boolean hasAlreadyApplied(
            User applicant,
            Long internshipId
    );

    List<Application> getOfferApplications(
            Long offerId,
            User currentUser,
            String applicantName,
            LocalDate submittedDate
    );
}