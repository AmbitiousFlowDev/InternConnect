package uca.github.org;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uca.github.org.models.Notification;
import uca.github.org.models.NotificationPreference;
import uca.github.org.models.User;
import uca.github.org.repositories.NotificationRepository;
import uca.github.org.services.NotificationPreferenceService;
import uca.github.org.services.NotificationServiceImpl;
import uca.github.org.services.RealtimeNotificationSender;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceService notificationPreferenceService;

    @Mock
    private RealtimeNotificationSender realtimeNotificationSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void createNotification_ShouldSaveAndSend_WhenPreferenceAllowsType() {
        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .build();

        NotificationPreference preference = NotificationPreference.builder()
                .user(user)
                .applicationNotifications(true)
                .realtimeEnabled(true)
                .build();

        Notification savedNotification = Notification.builder()
                .id(10L)
                .user(user)
                .type(Notification.NotificationType.APPLICATION)
                .title("Nouvelle candidature")
                .content("Un candidat a postulé à votre offre.")
                .isRead(false)
                .build();

        when(notificationPreferenceService.getOrCreatePreference(user)).thenReturn(preference);
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Optional<Notification> result = notificationService.createNotification(
                user,
                Notification.NotificationType.APPLICATION,
                "Nouvelle candidature",
                "Un candidat a postulé à votre offre.",
                "/applications/offers/1"
        );

        assertTrue(result.isPresent());
        verify(notificationRepository).save(any(Notification.class));
        verify(realtimeNotificationSender).sendToUser(eq(user), any());
    }

    @Test
    void createNotification_ShouldNotSave_WhenPreferenceDisablesType() {
        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .build();

        NotificationPreference preference = NotificationPreference.builder()
                .user(user)
                .messageNotifications(false)
                .realtimeEnabled(true)
                .build();

        when(notificationPreferenceService.getOrCreatePreference(user)).thenReturn(preference);

        Optional<Notification> result = notificationService.createNotification(
                user,
                Notification.NotificationType.MESSAGE,
                "Nouveau message",
                "Vous avez reçu un message.",
                "/messages"
        );

        assertTrue(result.isEmpty());
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(realtimeNotificationSender, never()).sendToUser(any(), any());
    }

    @Test
    void markAsRead_ShouldUpdateNotification_WhenNotificationBelongsToUser() {
        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .build();

        Notification notification = Notification.builder()
                .id(10L)
                .user(user)
                .type(Notification.NotificationType.SYSTEM)
                .title("Info")
                .content("Bienvenue")
                .isRead(false)
                .build();

        when(notificationRepository.findByIdAndUser(10L, user)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L, user);

        assertTrue(notification.getIsRead());
        assertNotNull(notification.getReadAt());
        verify(notificationRepository).save(notification);
    }

    @Test
    void countUnread_ShouldDelegateToRepository() {
        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .build();

        when(notificationRepository.countByUserAndIsReadFalse(user)).thenReturn(3L);

        long count = notificationService.countUnread(user);

        assertEquals(3L, count);
    }
}