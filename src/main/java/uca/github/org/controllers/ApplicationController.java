package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.services.ApplicationService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // =========================
    // POSTULER A UNE OFFRE
    // =========================

    @PostMapping("/apply")
    public String apply(
            @RequestParam Long offerId,
            @RequestParam(required = false) String coverLetter,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {

            applicationService.apply(
                    currentUser,
                    offerId,
                    coverLetter
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Candidature envoyée avec succès !"
            );

        } catch (IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

        } catch (Exception e) {

            e.printStackTrace();

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Une erreur est survenue."
            );
        }

        return "redirect:/internships";
    }

    // =========================
    // PAGE DES CANDIDATURES
    // =========================

    @GetMapping("/status")
    public String applicationStatus(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) Application.ApplicationStatus status,
            Model model) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Application> applications;

        if (status != null) {
            applications = applicationService.getUserApplicationsByStatus(currentUser, status);
        } else {
            applications = applicationService.getUserApplications(currentUser);
        }

        Map<Application.ApplicationStatus, Long> summary =
                applicationService.getStatusSummary(currentUser);

        model.addAttribute("applications", applications);
        model.addAttribute("statuses", Application.ApplicationStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusSummary", summary);
        model.addAttribute("user", currentUser);

        return "applications";
    }

    // =========================
    // RETIRER UNE CANDIDATURE
    // =========================

    @PostMapping("/withdraw/{id}")
    public String withdrawApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {

            applicationService.withdraw(currentUser, id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Candidature retirée avec succès."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/applications/status";
    }
}