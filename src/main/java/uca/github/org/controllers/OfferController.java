package uca.github.org.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import uca.github.org.forms.OfferPublicationForm;
import uca.github.org.models.User;
import uca.github.org.services.OfferService;

@Controller
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping("/offers/publish")
    public String showPublishForm(
            @AuthenticationPrincipal User currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getRole() != User.Role.POSTER && currentUser.getRole() != User.Role.ADMIN) {
            redirectAttributes.addFlashAttribute("errorMessage", "Seuls les posteurs peuvent publier une offre.");
            return "redirect:/dashboard";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("offerPublicationForm", new OfferPublicationForm());

        return "pages/offers/publish";
    }

    @PostMapping("/offers/publish")
    public String publishOffer(
            OfferPublicationForm offerPublicationForm,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        offerService.publishOffer(offerPublicationForm, currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Votre offre a bien été publiée.");
        return "redirect:/offers/publish";
    }
}
