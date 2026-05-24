package uca.github.org.services;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uca.github.org.models.Notification;
import uca.github.org.models.NotificationPreference;
import uca.github.org.models.User;
import uca.github.org.records.NotificationDto;
import uca.github.org.repositories.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceService notificationPreferenceService;
    private final RealtimeNotificationSender realtimeNotificationSender;

    @Override
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Notification> getRecentNotifications(User user) {
        return notificationRepository.findTop10ByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public long countUnread(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public Optional<Notification> createNotification(
            User user,
            Notification.NotificationType type,
            String title,
            String content,
            String targetUrl) {

        NotificationPreference preference = notificationPreferenceService.getOrCreatePreference(user);

        if (!isAllowed(type, preference)) {
            return Optional.empty();
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .content(content)
                .targetUrl(targetUrl)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        if (preference.isRealtimeEnabled()) {
            realtimeNotificationSender.sendToUser(user, NotificationDto.from(savedNotification));
        }

        return Optional.of(savedNotification);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new IllegalArgumentException("Notification introuvable."));

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);

        notifications.stream()
                .filter(notification -> !Boolean.TRUE.equals(notification.getIsRead()))
                .forEach(Notification::markAsRead);

        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new IllegalArgumentException("Notification introuvable."));

        notificationRepository.delete(notification);
    }

    private boolean isAllowed(Notification.NotificationType type, NotificationPreference preference) {
        return switch (type) {
            case APPLICATION -> preference.isApplicationNotifications();
            case MESSAGE -> preference.isMessageNotifications();
            case STATUS_UPDATE -> preference.isStatusUpdateNotifications();
            case OFFER, SYSTEM -> true;
        };
    }
}