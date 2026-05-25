package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uca.github.org.matching.CandidateMatchingService;
import uca.github.org.models.User;
import uca.github.org.repositories.InternshipRepository;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipRepository internshipRepository;
    private final CandidateMatchingService candidateMatchingService;

    @GetMapping("/internships")
    public String internships(
            Model model,
            @AuthenticationPrincipal User currentUser
    ) {

        model.addAttribute(
                "internships",
                internshipRepository.findAll()
        );

        model.addAttribute("user", currentUser);

        return "internships";
    }

    @GetMapping("/internships/{id}")
    public String internshipDetails(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        var offer = internshipRepository.findById(id);
        if (offer.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Offre introuvable.");
            return "redirect:/internships";
        }

        model.addAttribute("offer", offer.get());
        model.addAttribute("user", currentUser);

        if (canViewCandidateMatches(currentUser)) {
            model.addAttribute("candidateMatches", candidateMatchingService.findMatchesForOffer(id));
        }

        return "pages/offers/details";
    }

    private boolean canViewCandidateMatches(User user) {
        return user != null && (user.getRole() == User.Role.POSTER || user.getRole() == User.Role.ADMIN);
    }
}
