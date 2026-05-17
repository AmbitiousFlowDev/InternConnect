package uca.github.org.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.services.ApplicationService;
import java.util.*;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/status")
    public String getApplicationStatus(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        if (currentUser == null) return "redirect:/login";

        List<Application> applications;
        if (status != null && !status.isBlank()) {
            try {
                Application.ApplicationStatus appStatus =
                        Application.ApplicationStatus.valueOf(status.toUpperCase());
                applications = applicationService.getUserApplicationsByStatus(currentUser, appStatus);
                model.addAttribute("selectedStatus", appStatus);
            } catch (IllegalArgumentException e) {
                applications = applicationService.getUserApplications(currentUser);
            }
        } else {
            applications = applicationService.getUserApplications(currentUser);
        }

        model.addAttribute("applications", applications);
        model.addAttribute("statuses", Application.ApplicationStatus.values());
        model.addAttribute("statusSummary", applicationService.getStatusSummary(currentUser));
        model.addAttribute("user", currentUser);
        return "pages/applications";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) return "redirect:/login";

        try {
            Application.ApplicationStatus newStatus =
                    Application.ApplicationStatus.valueOf(status.toUpperCase());
            applicationService.updateApplicationStatus(id, newStatus);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            // log erreur
        }
        return "redirect:/applications/status";
    }

    // NOUVEAU — postuler
    @PostMapping("/apply")
    public String apply(
            @RequestParam Long offerId,
            @RequestParam(required = false) String coverLetter,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) return "redirect:/login";

        try {
            applicationService.apply(currentUser, offerId, coverLetter);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Candidature envoyée avec succès ! 🎉");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Une erreur est survenue. Veuillez réessayer.");
        }

        return "redirect:/internships/" + offerId;
    }

    // NOUVEAU — retirer candidature
    @PostMapping("/withdraw/{applicationId}")
    public String withdraw(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) return "redirect:/login";

        try {
            applicationService.withdraw(currentUser, applicationId);
            redirectAttributes.addFlashAttribute("successMessage", "Candidature retirée.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/applications/status";
    }
}