package uca.github.org.services;

import java.util.List;
import java.util.Optional;

import uca.github.org.models.Notification;
import uca.github.org.models.User;

public interface NotificationService {

    List<Notification> getUserNotifications(User user);

    List<Notification> getRecentNotifications(User user);

    long countUnread(User user);

    Optional<Notification> createNotification(
            User user,
            Notification.NotificationType type,
            String title,
            String content,
            String targetUrl
    );

    void markAsRead(Long notificationId, User user);

    void markAllAsRead(User user);

    void deleteNotification(Long notificationId, User user);
}