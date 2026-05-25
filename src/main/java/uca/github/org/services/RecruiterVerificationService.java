package uca.github.org.services;

import uca.github.org.models.RecruiterVerification;
import uca.github.org.models.User;

import java.util.List;
import java.util.Optional;

public interface RecruiterVerificationService {
    RecruiterVerification submit(User recruiter, String companyName, String proofDescription);

    RecruiterVerification review(Long verificationId, RecruiterVerification.VerificationStatus status, String adminComment, User admin);

    Optional<RecruiterVerification> findLatestFor(User recruiter);

    List<RecruiterVerification> findAll();

    boolean isApproved(User recruiter);
}
