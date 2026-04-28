package uca.github.org.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import uca.github.org.models.User;

/**
 * Controller for handling dashboard-related requests.
 * This controller will manage the display and functionality of the user's
 * dashboard.
 */
@Controller
public class DashboardController {
    /**
     * Dashboard (protected)
     * Handles GET requests to "/dashboard" and displays the dashboard page. This
     * page is protected and requires authentication. If the user is authenticated,
     * their username is added to the model to be displayed on the dashboard page.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("user", currentUser);
        return "pages/dashboard";
    }
}
