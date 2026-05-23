package uca.github.org.records;

import java.time.LocalDateTime;

import uca.github.org.models.Notification;

public record NotificationDto(
        Long id,
        String type,
        String title,
        String content,
        String targetUrl,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationDto from(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getType() != null ? notification.getType().name() : "SYSTEM",
                notification.getTitle(),
                notification.getContent(),
                notification.getTargetUrl(),
                Boolean.TRUE.equals(notification.getIsRead()),
                notification.getCreatedAt()
        );
    }
}