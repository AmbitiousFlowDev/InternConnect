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

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final ObjectProvider<ProfileService> profileService;
    private final ObjectProvider<ApplicationRepository> applicationRepository;
    private final ObjectProvider<BookmarkRepository> bookmarkRepository;

    @GetMapping({ "/home", "/home.html" })
    public String home(@AuthenticationPrincipal User authenticatedUser, Model model) {
        model.addAttribute("internships", homeService.getLatestInternships());
        model.addAttribute("stats", homeService.getPlatformStats());

        if (authenticatedUser != null) {
            model.addAttribute("user", authenticatedUser);
            model.addAttribute("userDisplayName", getDisplayName(authenticatedUser));
            model.addAttribute("userInitials", getInitials(authenticatedUser));
            model.addAttribute("profileCompleteness", getProfileCompleteness(authenticatedUser));
            model.addAttribute("applicationCount", getApplicationCount(authenticatedUser));
            model.addAttribute("savedCount", getSavedCount(authenticatedUser));
        }

        return "pages/home";
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

    private String firstLetter(String value) {
        return value.isBlank() ? "" : value.substring(0, 1);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
