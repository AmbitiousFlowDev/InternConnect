package uca.github.org.services;

import uca.github.org.models.Application;
import uca.github.org.models.User;
import java.util.List;

public interface ApplicationService {
    List<Application> getUserApplications(User user);
    List<Application> getUserApplicationsByStatus(
            User user, Application.ApplicationStatus status);
}