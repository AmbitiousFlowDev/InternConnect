package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.services.ApplicationService;

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

        if (status != null && !status.isBlank()) {
            try {
                Application.ApplicationStatus appStatus =
                        Application.ApplicationStatus.valueOf(status.toUpperCase());
                model.addAttribute("applications",
                        applicationService.getUserApplicationsByStatus(currentUser, appStatus));
                model.addAttribute("selectedStatus", status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Statut invalide → on retourne tout
                model.addAttribute("applications",
                        applicationService.getUserApplications(currentUser));
            }
        } else {
            model.addAttribute("applications",
                    applicationService.getUserApplications(currentUser));
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("statuses", Application.ApplicationStatus.values());
        return "pages/applications";
    }
}