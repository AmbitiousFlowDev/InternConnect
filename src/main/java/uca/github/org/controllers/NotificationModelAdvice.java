package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uca.github.org.models.User;
import uca.github.org.services.NotificationPreferenceService;
import uca.github.org.services.NotificationService;

@ControllerAdvice
@RequiredArgsConstructor
public class NotificationModelAdvice {

    private final ObjectProvider<NotificationService> notificationService;
    private final ObjectProvider<NotificationPreferenceService> notificationPreferenceService;

    @ModelAttribute
    public void addNotificationAttributes(
            @AuthenticationPrincipal User currentUser,
            Model model) {

        if (currentUser == null) {
            return;
        }

        NotificationService service = notificationService.getIfAvailable();
        NotificationPreferenceService preferenceService = notificationPreferenceService.getIfAvailable();

        if (service != null) {
            model.addAttribute("unreadNotificationsCount", service.countUnread(currentUser));
        }

        if (preferenceService != null) {
            model.addAttribute(
                    "notificationRealtimeEnabled",
                    preferenceService.getOrCreatePreference(currentUser).isRealtimeEnabled()
            );
        }
    }
}