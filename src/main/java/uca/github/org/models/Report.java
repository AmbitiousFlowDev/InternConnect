package uca.github.org.models;

import jakarta.persistence.*;
import lombok.*;
 
import java.time.LocalDate;


@Entity
@Table(name = "reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
 
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin;
 
    @Column(columnDefinition = "TEXT")
    private String reason;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;
 
    @Column(name = "reported_at")
    private LocalDate reportedAt;
 
    public enum ReportStatus {
        PENDING, PROCESSED, ARCHIVED
    }
}