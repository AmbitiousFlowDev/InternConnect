package uca.github.org.controllers;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uca.github.org.forms.OfferEditForm;
import uca.github.org.forms.OfferPublicationForm;
import uca.github.org.models.Internship;
import uca.github.org.models.User;
import uca.github.org.repositories.InternshipRepository;
import uca.github.org.services.OfferService;

@Controller
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final InternshipRepository internshipRepository;

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

    @GetMapping("/offers/my")
    public String myOffers(
            @AuthenticationPrincipal User currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getRole() != User.Role.POSTER && currentUser.getRole() != User.Role.ADMIN) {
            redirectAttributes.addFlashAttribute("errorMessage", "Seuls les posteurs peuvent gérer leurs offres.");
            return "redirect:/dashboard";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("offers", internshipRepository.findByPosterAndStatusNotOrderByPublishedAtDescIdDesc(
                currentUser,
                Internship.InternshipStatus.ARCHIVED
        ));

        return "pages/offers/my-offers";
    }

    @GetMapping("/offers/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Internship> optionalOffer = internshipRepository.findById(id);

        if (optionalOffer.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Offre introuvable.");
            return "redirect:/offers/my";
        }

        Internship offer = optionalOffer.get();

        if (!offer.getPoster().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas le droit de modifier cette offre.");
            return "redirect:/offers/my";
        }

        OfferEditForm form = new OfferEditForm();
        form.setId(offer.getId());
        form.setTitle(offer.getTitle());
        form.setCompany(offer.getCompany());
        form.setSector(offer.getSector());
        form.setLocation(offer.getLocation());
        form.setDuration(offer.getDuration());
        form.setSalary(offer.getSalary());
        form.setDescription(offer.getDescription());

        form.setRequiredSkills(offer.getRequiredSkills());
        form.setEducationLevel(offer.getEducationLevel());
        form.setSoftSkills(offer.getSoftSkills());
        form.setDesiredProfile(offer.getDesiredProfile());
        form.setLanguages(offer.getLanguages());

        form.setRequestedDocuments(offer.getRequestedDocuments());

        if (offer.getExpiresAt() != null) {
            form.setExpiresAt(offer.getExpiresAt().format(DateTimeFormatter.ISO_DATE));
        }

        form.setContactEmail(offer.getContactEmail());
        form.setTermsAccepted(true);

        model.addAttribute("user", currentUser);
        model.addAttribute("offerEditForm", form);

        return "pages/offers/edit";
    }

    @GetMapping("/bookmarks")
    public String savedOffers(
            @AuthenticationPrincipal User currentUser,
            Model model) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("bookmarks", offerService.getSavedOffers(currentUser));

        return "pages/offers/saved";
    }

    @PostMapping("/offers/publish")
    public String publishOffer(
            @Valid @ModelAttribute("offerPublicationForm") OfferPublicationForm offerPublicationForm,
            BindingResult bindingResult,
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

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUser);
            return "pages/offers/publish";
        }

        offerService.publishOffer(offerPublicationForm, currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Votre offre a bien été publiée.");
        return "redirect:/offers/publish";
    }

    @PostMapping("/offers/update")
    public String updateOffer(
            @Valid @ModelAttribute("offerEditForm") OfferEditForm offerEditForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUser);
            return "pages/offers/edit";
        }

        try {
            offerService.updateOffer(offerEditForm, currentUser);

            redirectAttributes.addFlashAttribute("successMessage", "L'offre a bien été modifiée.");
            return "redirect:/offers/my";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("user", currentUser);
            model.addAttribute("errorMessage", ex.getMessage());
            return "pages/offers/edit";

        } catch (DataAccessException ex) {
            model.addAttribute("user", currentUser);
            model.addAttribute("errorMessage", "Impossible de modifier l'offre pour le moment.");
            return "pages/offers/edit";
        }
    }

    @PostMapping("/offers/delete")
    public String deleteOffer(
            @RequestParam Long id,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            offerService.deleteOffer(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "L'offre a bien été supprimée.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de supprimer l'offre pour le moment.");
        }

        return "redirect:/offers/my";
    }

    @PostMapping("/offers/save")
    public String saveOffer(
            @RequestParam Long id,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            offerService.saveOffer(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Offre sauvegardée.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de sauvegarder l'offre pour le moment.");
        }

        return "redirect:/home";
    }

    @PostMapping("/offers/unsave")
    public String removeSavedOffer(
            @RequestParam Long id,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            offerService.removeSavedOffer(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Offre retirée des sauvegardes.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de retirer l'offre pour le moment.");
        }

        return "redirect:/bookmarks";
    }

    @GetMapping("/offers/search")
    public String searchOffers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) String company,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        var results = offerService.searchOffers(keyword, location, sector, duration, company);
        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);

        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            model.addAttribute("userDisplayName", getDisplayName(currentUser));
            model.addAttribute("userInitials", getInitials(currentUser));
        }

        return "pages/offers/search";
    }

    private String getDisplayName(User user) {
        String firstName = clean(user.getFirstName());
        String lastName = clean(user.getLastName());
        String fullName = (firstName + " " + lastName).trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        String email = clean(user.getEmail());
        return email.isBlank() ? "Utilisateur" : email;
    }

    private String getInitials(User user) {
        String firstName = clean(user.getFirstName());
        String lastName = clean(user.getLastName());

        String initials = firstLetter(firstName) + firstLetter(lastName);
        if (!initials.isBlank()) {
            return initials.toUpperCase();
        }

        String email = clean(user.getEmail());
        return email.isBlank() ? "U" : firstLetter(email).toUpperCase();
    }

    private String firstLetter(String value) {
        return value.isBlank() ? "" : value.substring(0, 1);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}