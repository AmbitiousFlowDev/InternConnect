package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.repositories.InternshipRepository;

@Controller
@RequiredArgsConstructor
public class ConversationController {

    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;

    @PostMapping("/conversations/start")
    public String startConversation(
            @RequestParam(required = false) Long internshipId,
            @RequestParam(required = false) Long applicationId,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            Long partnerId = resolvePartnerId(currentUser, internshipId, applicationId);
            return "redirect:/messages/" + partnerId;
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return internshipId == null ? "redirect:/dashboard" : "redirect:/internships/" + internshipId;
        }
    }

    private Long resolvePartnerId(User currentUser, Long internshipId, Long applicationId) {
        if (currentUser.getRole() == User.Role.USER && internshipId != null) {
            var internship = internshipRepository.findById(internshipId)
                    .orElseThrow(() -> new IllegalArgumentException("Offre introuvable."));
            if (internship.getPoster() == null) {
                throw new IllegalArgumentException("Recruteur introuvable pour cette offre.");
            }
            return internship.getPoster().getId();
        }

        if ((currentUser.getRole() == User.Role.POSTER || currentUser.getRole() == User.Role.ADMIN) && applicationId != null) {
            var application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable."));
            var internship = application.getInternship();
            if (currentUser.getRole() == User.Role.POSTER
                    && (internship == null || internship.getPoster() == null || !internship.getPoster().getId().equals(currentUser.getId()))) {
                throw new IllegalArgumentException("Vous ne pouvez contacter que les candidats de vos offres.");
            }
            if (application.getApplicant() == null) {
                throw new IllegalArgumentException("Candidat introuvable.");
            }
            return application.getApplicant().getId();
        }

        throw new IllegalArgumentException("Impossible de démarrer cette conversation.");
    }
}
