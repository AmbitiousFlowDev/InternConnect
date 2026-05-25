package uca.github.org.controllers;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.repositories.BookmarkRepository;
import uca.github.org.services.HomeService;
import uca.github.org.services.ProfileService;
import uca.github.org.services.UserDisplayService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final ObjectProvider<ProfileService> profileService;
    private final ObjectProvider<ApplicationRepository> applicationRepository;
    private final ObjectProvider<BookmarkRepository> bookmarkRepository;
    private final UserDisplayService userDisplayService;

    @GetMapping({ "/home", "/home.html" })
    public String home(@AuthenticationPrincipal User authenticatedUser, Model model) {
        model.addAttribute("internships", homeService.getLatestInternships());
        model.addAttribute("stats", homeService.getPlatformStats());

        if (authenticatedUser != null) {
            model.addAttribute("user", authenticatedUser);
            model.addAttribute("userDisplayName", userDisplayService.getDisplayName(authenticatedUser));
            model.addAttribute("userInitials", userDisplayService.getInitials(authenticatedUser));
            model.addAttribute("profileCompleteness", getProfileCompleteness(authenticatedUser));
            model.addAttribute("applicationCount", getApplicationCount(authenticatedUser));
            model.addAttribute("savedCount", getSavedCount(authenticatedUser));
        }

        return "pages/home";
    }

    private int getProfileCompleteness(User user) {
        ProfileService service = profileService.getIfAvailable();
        return service == null ? 0 : service.calculateCompleteness(user);
    }

    private long getApplicationCount(User user) {
        ApplicationRepository repository = applicationRepository.getIfAvailable();
        return repository == null ? 0 : repository.countByApplicant(user);
    }

    private long getSavedCount(User user) {
        BookmarkRepository repository = bookmarkRepository.getIfAvailable();
        return repository == null ? 0 : repository.countByUser(user);
    }

}
