package uca.github.org.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import uca.github.org.models.User;

@Controller
public class OfferController {

    @GetMapping("/offers/publish")
    public String showPublishForm(@AuthenticationPrincipal User currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        return "pages/offers/publish";
    }
}
