package uca.github.org.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "bookmarks",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_bookmark_user_internship",
        columnNames = {"user_id", "internship_id"}
    ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;

    private LocalDate addedAt;

    @Builder.Default
    private Boolean alertEnabled = false;

    @PrePersist
    public void prePersist() {
        this.addedAt = LocalDate.now();
    }
}