package uca.github.org.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.repositories.BookmarkRepository;
import uca.github.org.repositories.RecommendationRepository;
import uca.github.org.services.ApplicationService;

/**
 * Controller for handling dashboard-related requests.
 * This controller manages the display and functionality of the user's dashboard.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ApplicationService applicationService;
    private final ApplicationRepository applicationRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RecommendationRepository recommendationRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);

        long totalApps = applicationRepository.countByApplicant(currentUser);
        long inReview = applicationRepository.countByApplicantAndStatus(
                currentUser,
                Application.ApplicationStatus.UNDER_REVIEW
        );
        long savedCount = bookmarkRepository.countByUser(currentUser);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalApplications", totalApps);
        stats.put("inReview", inReview);
        stats.put("saved", savedCount);
        stats.put("interviews", 2);

        model.addAttribute("stats", stats);

        model.addAttribute(
                "applications",
                applicationService.getUserApplications(currentUser)
                        .stream()
                        .limit(5)
                        .collect(Collectors.toList())
        );

        model.addAttribute("bookmarks", bookmarkRepository.findByUserOrderByAddedAtDesc(currentUser));
        model.addAttribute("recommendations", recommendationRepository.findByUserOrderByScoreDesc(currentUser));

        return "pages/dashboard";
    }
}