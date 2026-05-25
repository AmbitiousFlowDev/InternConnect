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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

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
            applicationService.apply(currentUser, offerId, coverLetter);
            redirectAttributes.addFlashAttribute("successMessage", "Votre candidature a été envoyée avec succès.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur est survenue lors de l'envoi de la candidature.");
        }

        return "redirect:/internships/" + offerId;
    }

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
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de consulter les candidatures de cette offre.");
            return "redirect:/offers/my";
        }
    }

    @GetMapping
    public String applications(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) String status,
            Model model
    ) {
        return renderApplicationStatus(currentUser, status, model);
    }

    @GetMapping("/status")
    public String applicationStatus(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) String status,
            Model model
    ) {
        return renderApplicationStatus(currentUser, status, model);
    }

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
            applicationService.updateApplicationStatus(id, status, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Le statut de la candidature a été mis à jour.");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de mettre à jour le statut de la candidature.");
        }

        if (offerId != null) {
            return "redirect:/applications/offers/" + offerId;
        }

        return "redirect:/applications/status";
    }

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
            redirectAttributes.addFlashAttribute("successMessage", "Candidature retirée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/applications/status";
    }

    private String renderApplicationStatus(User currentUser, String status, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        Application.ApplicationStatus selectedStatus = parseFilterStatus(status);
        List<Application> applications = selectedStatus == null
                ? applicationService.getUserApplications(currentUser)
                : applicationService.getUserApplicationsByStatus(currentUser, selectedStatus);

        Map<Application.ApplicationStatus, Long> summary = applicationService.getStatusSummary(currentUser);

        model.addAttribute("applications", applications);
        model.addAttribute("statuses", filterStatuses());
        model.addAttribute("selectedStatus", selectedStatus);
        model.addAttribute("statusSummary", summary);
        model.addAttribute("user", currentUser);

        if (status != null && !status.isBlank() && selectedStatus == null && !"ALL".equalsIgnoreCase(status)) {
            model.addAttribute("errorMessage", "Filtre de statut ignoré car il est invalide.");
        }

        return "pages/applications";
    }

    private Application.ApplicationStatus parseFilterStatus(String value) {
        if (value == null || value.isBlank() || "ALL".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            Application.ApplicationStatus status = Application.ApplicationStatus.valueOf(value.trim().toUpperCase());
            return Arrays.asList(filterStatuses()).contains(status) ? status : null;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private Application.ApplicationStatus[] filterStatuses() {
        return new Application.ApplicationStatus[] {
                Application.ApplicationStatus.SUBMITTED,
                Application.ApplicationStatus.UNDER_REVIEW,
                Application.ApplicationStatus.ACCEPTED,
                Application.ApplicationStatus.REJECTED
        };
    }
}
