package uca.github.org.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling authentication-related requests, such as login and
 * registration.
 * This controller will manage user authentication processes, including
 * displaying login and registration forms,
 * and processing authentication requests. It will also handle any necessary
 * redirects after successful login or registration.
 */
@Controller
public class AuthController {
    /**
     * Login page (public)
     * Handles GET requests to "/login" and displays the login form. It also checks
     * for any error, logout, or session expiration messages and adds them to the
     * model to be displayed on the login page.
     */
    @GetMapping("/login")
    public String login(Model model, String error, String logout, String expired) {

        if (error != null) {
            model.addAttribute("error", "Email ou mot de passe invalide");
        }

        if (logout != null) {
            model.addAttribute("message", "Déconnexion réussie");
        }

        if (expired != null) {
            model.addAttribute("error", "Session expirée, veuillez vous reconnecter");
        }

        return "auth/login"; // maps to login.html
    }

    /**
     * Home page (public)
     * Handles GET requests to "/" and "/home" and displays the home page. This page
     * is accessible to all users, regardless of their authentication status.
     */
    @GetMapping({ "/", "/home" })
    public String home() {
        return "pages/home";
    }

    /**
     * Dashboard (protected)
     * Handles GET requests to "/dashboard" and displays the dashboard page. This
     * page is protected and requires authentication. If the user is authenticated,
     * their username is added to the model to be displayed on the dashboard page.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "pages/dashboard";
    }

    /**
     * Registration page (public)
     * Handles GET requests to "/register" and displays the registration form.
     * This page is accessible to all users, regardless of their authentication
     * status, and allows new users to create an account.
     */
    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }
}