package uca.github.org.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.services.ApplicationService;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/offers/{offerId}")
    public String getOfferApplications(
            @PathVariable Long offerId,
            @RequestParam(required = false) String applicantName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submittedDate,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        if (currentUser == null) return "redirect:/login";

        try {
            List<Application> applications = applicationService.getOfferApplications(
                    offerId,
                    currentUser,
                    applicantName,
                    submittedDate);

            model.addAttribute("applications", applications);
            model.addAttribute("offerId", offerId);
            model.addAttribute("applicantName", applicantName);
            model.addAttribute("submittedDate", submittedDate);
            model.addAttribute("user", currentUser);

            return "pages/offers/applicants";
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return "redirect:/offers/my";
        }
    }

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
                applications = applicationService
                        .getUserApplicationsByStatus(currentUser, appStatus);
                model.addAttribute("selectedStatus", appStatus);
            } catch (IllegalArgumentException e) {
                applications = applicationService.getUserApplications(currentUser);
            }
        } else {
            applications = applicationService.getUserApplications(currentUser);
        }

        model.addAttribute("applications", applications);
        model.addAttribute("statuses", Application.ApplicationStatus.values());
        model.addAttribute("statusSummary",
                applicationService.getStatusSummary(currentUser));
        model.addAttribute("user", currentUser);
        return "pages/applications";
    }
    // ApplicationController.java — ajoute cet endpoint
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
}
