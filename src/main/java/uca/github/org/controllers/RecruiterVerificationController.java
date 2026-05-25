package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uca.github.org.models.RecruiterVerification;
import uca.github.org.models.User;
import uca.github.org.services.RecruiterVerificationService;

@Controller
@RequiredArgsConstructor
public class RecruiterVerificationController {

    private final RecruiterVerificationService recruiterVerificationService;

    @GetMapping("/recruiter-verification")
    public String showVerification(@AuthenticationPrincipal User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (currentUser.getRole() != User.Role.POSTER) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("verification", recruiterVerificationService.findLatestFor(currentUser).orElse(null));
        return "pages/recruiter-verification";
    }

    @PostMapping("/recruiter-verification")
    public String submitVerification(
            @RequestParam String companyName,
            @RequestParam String proofDescription,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            recruiterVerificationService.submit(currentUser, companyName, proofDescription);
            redirectAttributes.addFlashAttribute("successMessage", "Votre demande de vérification a été envoyée.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/recruiter-verification";
    }

    @GetMapping("/admin/recruiter-verifications")
    public String listVerifications(@AuthenticationPrincipal User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (currentUser.getRole() != User.Role.ADMIN) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("verifications", recruiterVerificationService.findAll());
        return "pages/admin/recruiter-verifications";
    }

    @PostMapping("/admin/recruiter-verifications/{id}/review")
    public String reviewVerification(
            @PathVariable Long id,
            @RequestParam RecruiterVerification.VerificationStatus status,
            @RequestParam(required = false) String adminComment,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            recruiterVerificationService.review(id, status, adminComment, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "La demande de vérification a été mise à jour.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/admin/recruiter-verifications";
    }
}
