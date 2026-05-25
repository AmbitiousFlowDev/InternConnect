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

@Controller
@RequiredArgsConstructor
public class PosterApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/poster/offers/{offerId}/applications")
    public String offerApplications(
            @PathVariable Long offerId,
            @RequestParam(required = false) String applicantName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submittedDate,
            @AuthenticationPrincipal User currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("applications", applicationService.getOfferApplications(offerId, currentUser, applicantName, submittedDate));
            model.addAttribute("offerId", offerId);
            model.addAttribute("applicantName", applicantName);
            model.addAttribute("submittedDate", submittedDate);
            model.addAttribute("user", currentUser);
            return "pages/offers/applicants";
        } catch (IllegalArgumentException | EntityNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de consulter les candidatures de cette offre.");
            return "redirect:/offers/my";
        }
    }

    @PostMapping("/poster/applications/{applicationId}/status")
    public String updateStatus(
            @PathVariable Long applicationId,
            @RequestParam Application.ApplicationStatus status,
            @RequestParam Long offerId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            applicationService.updateApplicationStatus(applicationId, status, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Le statut de la candidature a été mis à jour.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de mettre à jour le statut de la candidature.");
        }

        return "redirect:/poster/offers/" + offerId + "/applications";
    }
}
