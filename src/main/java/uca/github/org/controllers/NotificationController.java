package uca.github.org.controllers;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uca.github.org.models.Notification;
import uca.github.org.models.User;
import uca.github.org.records.NotificationDto;
import uca.github.org.repositories.UserRepository;
import uca.github.org.services.NotificationPreferenceService;
import uca.github.org.services.NotificationService;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPreferenceService notificationPreferenceService;
    private final UserRepository userRepository;

    @GetMapping
    public String index(
            @AuthenticationPrincipal User currentUser,
            Model model) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("notifications", notificationService.getUserNotifications(currentUser));
        model.addAttribute("notificationRealtimeEnabled",
                notificationPreferenceService.getOrCreatePreference(currentUser).isRealtimeEnabled());

        return "pages/notifications/index";
    }

    @PostMapping
    public String createNotification(
            @RequestParam Long userId,
            @RequestParam Notification.NotificationType type,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String targetUrl,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getRole() != User.Role.ADMIN) {
            redirectAttributes.addFlashAttribute("errorMessage", "Action non autorisée.");
            return "redirect:/notifications";
        }

        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        notificationService.createNotification(recipient, type, title, content, targetUrl);
        redirectAttributes.addFlashAttribute("successMessage", "Notification créée.");

        return "redirect:/notifications";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            notificationService.markAsRead(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Notification marquée comme lue.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead(
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        notificationService.markAllAsRead(currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Toutes les notifications ont été marquées comme lues.");

        return "redirect:/notifications";
    }

    @PostMapping("/{id}/delete")
    public String deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            notificationService.deleteNotification(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Notification supprimée.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/notifications";
    }

    @GetMapping("/api/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> unreadCount(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(currentUser)));
    }

    @GetMapping("/api/recent")
    @ResponseBody
    public ResponseEntity<?> recent(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                notificationService.getRecentNotifications(currentUser)
                        .stream()
                        .map(NotificationDto::from)
                        .toList()
        );
    }
}