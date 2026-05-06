package uca.github.org.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import java.util.List;

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
}