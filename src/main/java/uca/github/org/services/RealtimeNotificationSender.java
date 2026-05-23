package uca.github.org.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import uca.github.org.models.User;
import uca.github.org.records.NotificationDto;

@Service
@RequiredArgsConstructor
public class RealtimeNotificationSender {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(User user, NotificationDto notification) {
        if (user == null || user.getUsername() == null) {
            return;
        }

        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications",
                notification
        );
    }
}