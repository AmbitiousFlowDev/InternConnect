package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uca.github.org.models.Message;
import uca.github.org.models.User;
import uca.github.org.services.MessageService;

import java.util.List;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public String inbox(@AuthenticationPrincipal User currentUser, Model model) {
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("partners", messageService.getConversationPartners(currentUser));
        model.addAttribute("user", currentUser);
        model.addAttribute("partnerId", null);
        model.addAttribute("messages", List.of());
        return "pages/messages";
    }

    @GetMapping("/{userId}")
    public String conversation(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser,
            Model model) {
        if (currentUser == null) return "redirect:/login";
        List<Message> messages = messageService.getConversation(currentUser, userId);
        model.addAttribute("messages", messages);
        model.addAttribute("partnerId", userId);
        model.addAttribute("user", currentUser);
        model.addAttribute("partners", messageService.getConversationPartners(currentUser));
        return "pages/messages";
    }

    @PostMapping("/send")
    public String send(
            @RequestParam Long recipientId,
            @RequestParam String content,
            @RequestParam(required = false) Long internshipId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) return "redirect:/login";

        try {
            messageService.sendMessage(currentUser, recipientId, content, internshipId);
            redirectAttributes.addFlashAttribute("successMessage", "Message envoyé !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'envoi.");
        }

        return "redirect:/messages/" + recipientId;
    }
}