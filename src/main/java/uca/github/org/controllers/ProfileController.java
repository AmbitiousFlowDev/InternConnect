package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uca.github.org.dto.ProfileUpdateForm;
import uca.github.org.models.Profile;
import uca.github.org.models.User;
import uca.github.org.repositories.UserRepository;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;

    @GetMapping("/profile")
    @Transactional(readOnly = true)
    public String profile(Model model, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return "redirect:/login";

        // Reload entity from DB to safely access relationships for counts.
        User user = userRepository.findById(currentUser.getId()).orElse(currentUser);

        Profile profile = user.getProfile();
        ProfileUpdateForm form = ProfileUpdateForm.from(user, profile);

        int applicationCount = user.getApplications() != null ? user.getApplications().size() : 0;
        int savedCount = user.getBookmarks() != null ? user.getBookmarks().size() : 0;
        int profileCompleteness = calculateCompleteness(form);

        List<?> recentApplications = user.getApplications() == null ? List.of()
                : user.getApplications().stream()
                .sorted(Comparator.comparing(a -> a.getSubmittedAt(), Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .toList();

        List<?> recentBookmarks = user.getBookmarks() == null ? List.of()
                : user.getBookmarks().stream()
                .sorted(Comparator.comparing(b -> b.getAddedAt(), Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("form", form);
        model.addAttribute("applicationCount", applicationCount);
        model.addAttribute("savedCount", savedCount);
        model.addAttribute("profileCompleteness", profileCompleteness);
        model.addAttribute("recentApplications", recentApplications);
        model.addAttribute("recentBookmarks", recentBookmarks);
        return "pages/profile";
    }

    private static int calculateCompleteness(ProfileUpdateForm form) {
        int total = 7;
        int filled = 0;
        if (hasText(form.firstName())) filled++;
        if (hasText(form.lastName())) filled++;
        if (hasText(form.description())) filled++;
        if (hasText(form.skills())) filled++;
        if (hasText(form.experience())) filled++;
        if (hasText(form.education())) filled++;
        if (hasText(form.preferences())) filled++;
        return (int) Math.round((filled * 100.0) / total);
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @PostMapping("/profile")
    @Transactional
    public String updateProfile(
            @ModelAttribute("form") ProfileUpdateForm form,
            @AuthenticationPrincipal User principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) return "redirect:/login";

        User user = userRepository.findById(principal.getId()).orElse(principal);

        user.setFirstName(form.firstName());
        user.setLastName(form.lastName());

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        profile.setDescription(form.description());
        profile.setSkills(form.skills());
        profile.setExperience(form.experience());
        profile.setEducation(form.education());
        profile.setPreferences(form.preferences());
        profile.setCoverLetter(form.coverLetter());
        profile.setResumeFile(form.resumeFile());

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("saved", true);
        return "redirect:/profile";
    }
}
