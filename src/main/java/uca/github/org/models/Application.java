package uca.github.org.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    public enum ApplicationStatus {
        SUBMITTED, UNDER_REVIEW, ACCEPTED, REJECTED, WITHDRAWN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private String resumeFile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private LocalDateTime submittedAt;

    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.SUBMITTED;
        }
    }
}