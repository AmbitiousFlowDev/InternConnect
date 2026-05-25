package uca.github.org.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uca.github.org.models.RecruiterVerification;
import uca.github.org.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterVerificationRepository extends JpaRepository<RecruiterVerification, Long> {
    Optional<RecruiterVerification> findFirstByRecruiterOrderBySubmittedAtDesc(User recruiter);

    List<RecruiterVerification> findAllByOrderBySubmittedAtDesc();

    boolean existsByRecruiterAndStatus(User recruiter, RecruiterVerification.VerificationStatus status);
}
