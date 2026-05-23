package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uca.github.org.models.User;
import uca.github.org.services.NotificationPreferenceService;

@Controller
@RequestMapping("/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;

    @GetMapping
    public String editPreferences(
            @AuthenticationPrincipal User currentUser,
            Model model) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("preferences", notificationPreferenceService.getOrCreatePreference(currentUser));

        return "pages/notifications/preferences";
    }

    @PostMapping
    public String updatePreferences(
            @RequestParam(defaultValue = "false") boolean applicationNotifications,
            @RequestParam(defaultValue = "false") boolean messageNotifications,
            @RequestParam(defaultValue = "false") boolean statusUpdateNotifications,
            @RequestParam(defaultValue = "false") boolean realtimeEnabled,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        notificationPreferenceService.updatePreference(
                currentUser,
                applicationNotifications,
                messageNotifications,
                statusUpdateNotifications,
                realtimeEnabled
        );

        redirectAttributes.addFlashAttribute("successMessage", "Préférences de notification mises à jour.");

        return "redirect:/notifications/preferences";
    }
}