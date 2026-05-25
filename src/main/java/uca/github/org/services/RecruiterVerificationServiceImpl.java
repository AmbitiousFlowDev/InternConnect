package uca.github.org.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uca.github.org.models.RecruiterVerification;
import uca.github.org.models.User;
import uca.github.org.repositories.RecruiterVerificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruiterVerificationServiceImpl implements RecruiterVerificationService {

    private final RecruiterVerificationRepository recruiterVerificationRepository;

    @Override
    @Transactional
    public RecruiterVerification submit(User recruiter, String companyName, String proofDescription) {
        if (recruiter == null || recruiter.getRole() != User.Role.POSTER) {
            throw new IllegalArgumentException("Seuls les recruteurs peuvent soumettre une vérification.");
        }
        if (companyName == null || companyName.isBlank() || proofDescription == null || proofDescription.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'entreprise et le justificatif sont obligatoires.");
        }

        return recruiterVerificationRepository.save(RecruiterVerification.builder()
                .recruiter(recruiter)
                .companyName(companyName.trim())
                .proofDescription(proofDescription.trim())
                .status(RecruiterVerification.VerificationStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public RecruiterVerification review(Long verificationId, RecruiterVerification.VerificationStatus status, String adminComment, User admin) {
        if (admin == null || admin.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("Seuls les administrateurs peuvent traiter les vérifications.");
        }
        if (status != RecruiterVerification.VerificationStatus.APPROVED
                && status != RecruiterVerification.VerificationStatus.REJECTED) {
            throw new IllegalArgumentException("Statut de vérification invalide.");
        }

        RecruiterVerification verification = recruiterVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new EntityNotFoundException("Demande de vérification introuvable."));
        verification.setStatus(status);
        verification.setAdminComment(adminComment);
        verification.setReviewedAt(LocalDateTime.now());
        verification.setReviewedBy(admin);
        return recruiterVerificationRepository.save(verification);
    }

    @Override
    public Optional<RecruiterVerification> findLatestFor(User recruiter) {
        return recruiter == null ? Optional.empty() : recruiterVerificationRepository.findFirstByRecruiterOrderBySubmittedAtDesc(recruiter);
    }

    @Override
    public List<RecruiterVerification> findAll() {
        return recruiterVerificationRepository.findAllByOrderBySubmittedAtDesc();
    }

    @Override
    public boolean isApproved(User recruiter) {
        return recruiter != null && recruiterVerificationRepository.existsByRecruiterAndStatus(
                recruiter,
                RecruiterVerification.VerificationStatus.APPROVED
        );
    }
}
