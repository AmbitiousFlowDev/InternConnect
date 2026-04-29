package uca.github.org.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uca.github.org.models.Application;
import uca.github.org.models.User;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    long countByApplicant(User applicant);
    long countByApplicantAndStatus(User applicant, Application.ApplicationStatus status);
    List<Application> findByApplicantOrderBySubmittedAtDesc(User applicant);
}
