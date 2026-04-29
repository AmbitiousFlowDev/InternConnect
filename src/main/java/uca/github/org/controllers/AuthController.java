package uca.github.org.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import uca.github.org.models.User;
import uca.github.org.services.AuthService;

/**
 * Controller for handling authentication-related requests, such as login and
 * registration.
 * This controller will manage user authentication processes, including
 * displaying login and registration forms,
 * and processing authentication requests. It will also handle any necessary
 * redirects after successful login or registration.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
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

        return "auth/login";
    }
    /**
     * Home page (public)
     * Handles GET requests to "/home" and displays the home page. This page
     * is accessible to all users, regardless of their authentication status.
     */
    @GetMapping("/home")
    public String home() {
        return "pages/home";
    }
    /**
     * Registration page (public)
     * Handles GET requests to "/register" and displays the registration form.
     * This page is accessible to all users, regardless of their authentication
     * status, and allows new users to create an account.
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    /**
     * Process registration (public)
     * Handles POST requests to "/register" and processes the registration form submission. It checks if a user with the provided email already exists, and if so, it adds an error message to the model and redisplays the registration form. If the email is unique, it saves the new user and redirects to the login page with a success parameter.
     * @param user
     * @param model
     * @return
     */
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user, Model model) {
        if (authService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "An account with this email already exists.");
            return "auth/register";
        }
        authService.register(user);
        return "redirect:/login?success";
    }
}