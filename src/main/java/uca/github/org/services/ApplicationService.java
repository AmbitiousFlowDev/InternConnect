package uca.github.org.services;

import uca.github.org.models.Application;
import uca.github.org.models.User;
import java.util.List;
import java.util.* ;

public interface ApplicationService {
    List<Application> getUserApplications(User user);
    List<Application> getUserApplicationsByStatus(
            User user, Application.ApplicationStatus status);
    Application updateApplicationStatus(Long applicationId, Application.ApplicationStatus newStatus);
    Map<Application.ApplicationStatus, Long> getStatusSummary(User user);
}