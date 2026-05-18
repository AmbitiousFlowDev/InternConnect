package uca.github.org.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.services.ApplicationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // =========================
    // APPLY TO AN INTERNSHIP OFFER
    // =========================

    @PostMapping("/apply")
    public String apply(
            @RequestParam Long offerId,
            @RequestParam(required = false) String coverLetter,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes
    ) {
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

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Une erreur est survenue lors de l'envoi de la candidature."
            );
        }

        return "redirect:/internships";
    }

    // =========================
    // VIEW APPLICANTS OF ONE OFFER
    // =========================

    @GetMapping("/offers/{offerId}")
    public String getOfferApplications(
            @PathVariable Long offerId,
            @RequestParam(required = false) String applicantName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submittedDate,
            @AuthenticationPrincipal User currentUser,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            List<Application> applications = applicationService.getOfferApplications(
                    offerId,
                    currentUser,
                    applicantName,
                    submittedDate
            );

            model.addAttribute("applications", applications);
            model.addAttribute("offerId", offerId);
            model.addAttribute("applicantName", applicantName);
            model.addAttribute("submittedDate", submittedDate);
            model.addAttribute("user", currentUser);

            return "pages/offers/applicants";

        } catch (IllegalArgumentException | EntityNotFoundException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Impossible de consulter les candidatures de cette offre."
            );

            return "redirect:/offers/my";
        }
    }

    // =========================
    // VIEW CURRENT USER APPLICATIONS
    // =========================

    @GetMapping("/status")
    public String applicationStatus(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) Application.ApplicationStatus status,
            Model model
    ) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Application> applications;

        if (status != null) {
            applications = applicationService.getUserApplicationsByStatus(
                    currentUser,
                    status
            );
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

        return "pages/applications";
    }

    // =========================
    // UPDATE APPLICATION STATUS
    // =========================

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam Application.ApplicationStatus status,
            @RequestParam(required = false) Long offerId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes
    ) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            applicationService.updateApplicationStatus(id, status);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Statut de candidature mis à jour avec succès."
            );

        } catch (IllegalArgumentException | EntityNotFoundException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Impossible de mettre à jour le statut de la candidature."
            );
        }

        if (offerId != null) {
            return "redirect:/applications/offers/" + offerId;
        }

        return "redirect:/applications/status";
    }

    // =========================
    // WITHDRAW CURRENT USER APPLICATION
    // =========================

    @PostMapping("/withdraw/{id}")
    public String withdrawApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes
    ) {
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