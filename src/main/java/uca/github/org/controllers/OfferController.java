package uca.github.org.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uca.github.org.models.User;

@Controller
public class OfferController {

    @GetMapping("/offers/publish")
    public String showPublishForm(
            @AuthenticationPrincipal User currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getRole() != User.Role.POSTER && currentUser.getRole() != User.Role.ADMIN) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Seuls les posteurs peuvent publier une offre."
            );
            return "redirect:/dashboard";
        }

        model.addAttribute("user", currentUser);
        return "pages/offers/publish";
    }
}
