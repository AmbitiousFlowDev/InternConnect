package uca.github.org.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "bookmarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;

    @Column(name = "added_at")
    private LocalDate addedAt;

    @Column(name = "alert_enabled", nullable = false)
    private Boolean alertEnabled = false;
}
