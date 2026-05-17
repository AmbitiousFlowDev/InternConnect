package uca.github.org.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "internships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String company;

    private String sector;

    private String location;

    private String duration;

    private String salary;

    @Column(columnDefinition = "TEXT")
    private String requiredSkills;

    private String educationLevel;

    @Column(columnDefinition = "TEXT")
    private String softSkills;

    @Column(columnDefinition = "TEXT")
    private String desiredProfile;

    private String languages;

    @Column(columnDefinition = "TEXT")
    private String requestedDocuments;

    private String contactEmail;
    
    private LocalDate publishedAt;

    private LocalDate expiresAt;

    @Enumerated(EnumType.STRING)
    private InternshipStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id")
    private User poster;

    public enum InternshipStatus {
    ACTIVE,
    ARCHIVED
}
}