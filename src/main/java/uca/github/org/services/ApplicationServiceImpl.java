package uca.github.org.services;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import java.util.List;
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
}