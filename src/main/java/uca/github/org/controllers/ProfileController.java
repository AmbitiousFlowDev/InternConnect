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

import uca.github.org.models.Profile;
import uca.github.org.models.User;
import uca.github.org.records.ProfileDTO;
import uca.github.org.repositories.UserRepository;

import java.util.Comparator;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public String getProfilePage(@AuthenticationPrincipal User user, Model model) {
        if (user == null)
            return "redirect:/login";

        User currentUser = userRepository.findById(user.getId()).orElseThrow();

        Profile profile = currentUser.getProfile();
        ProfileDTO form = ProfileDTO.builder()
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .description(profile != null ? profile.getDescription() : "")
                .education(profile != null ? profile.getEducation() : "")
                .skills(profile != null ? profile.getSkills() : "")
                .experience(profile != null ? profile.getExperience() : "")
                .preferences(profile != null ? profile.getPreferences() : "")
                .build();

        long appCount = currentUser.getApplications().size();
        long savedCount = currentUser.getBookmarks().size();
        int completeness = calculateCompleteness(currentUser, profile);

        model.addAttribute("user", currentUser);
        model.addAttribute("form", form);
        model.addAttribute("applicationCount", appCount);
        model.addAttribute("savedCount", savedCount);
        model.addAttribute("profileCompleteness", completeness);
        model.addAttribute("recentApplications", currentUser.getApplications());
        model.addAttribute("recentBookmarks", currentUser.getBookmarks());

        return "pages/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal User user,
            @ModelAttribute("form") ProfileDTO profileDTO,
            RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findById(user.getId()).orElseThrow();

        currentUser.setFirstName(profileDTO.getFirstName());
        currentUser.setLastName(profileDTO.getLastName());

        Profile profile = currentUser.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(currentUser);
            currentUser.setProfile(profile);
        }

        profile.setDescription(profileDTO.getDescription());
        profile.setEducation(profileDTO.getEducation());
        profile.setSkills(profileDTO.getSkills());
        profile.setExperience(profileDTO.getExperience());
        profile.setPreferences(profileDTO.getPreferences());

        userRepository.save(currentUser);

        redirectAttributes.addFlashAttribute("saved", true);
        return "redirect:/profile";
    }

    private int calculateCompleteness(User user, Profile profile) {
        int points = 0;
        if (profile == null)
            return 20; 
        if (profile.getDescription() != null && !profile.getDescription().isEmpty())
            points += 20;
        if (profile.getEducation() != null && !profile.getEducation().isEmpty())
            points += 20;
        if (profile.getSkills() != null && !profile.getSkills().isEmpty())
            points += 20;
        if (profile.getExperience() != null && !profile.getExperience().isEmpty())
            points += 20;
        return 20 + points;
    }
}