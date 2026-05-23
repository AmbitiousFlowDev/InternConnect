package uca.github.org.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private boolean applicationNotifications = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean messageNotifications = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean statusUpdateNotifications = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean realtimeEnabled = true;
}