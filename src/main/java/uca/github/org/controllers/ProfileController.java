package uca.github.org.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uca.github.org.models.Profile;
import uca.github.org.models.User;
import uca.github.org.records.ProfileDTO;
import uca.github.org.repositories.UserRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import uca.github.org.services.ProfileService;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final ProfileService profileService;

    @GetMapping("/profile")
    public String getProfilePage(@AuthenticationPrincipal User user, Model model) {

        if (user == null) return "redirect:/login";

        User currentUser = userRepository.findById(user.getId()).orElseThrow();
        Profile profile = currentUser.getProfile();

        ProfileDTO form = ProfileDTO.builder().firstName(currentUser.getFirstName())
            .lastName(currentUser.getLastName()).description(profile != null ? profile.getDescription() : "")
            .education(profile != null ? profile.getEducation() : "")
            .skills(profile != null ? profile.getSkills() : "")
            .experience(profile != null ? profile.getExperience() : "")
            .preferences(profile != null ? profile.getPreferences() : "")
            .build();

        model.addAttribute("user", currentUser);
        model.addAttribute("form", form);
        model.addAttribute("applicationCount", currentUser.getApplications().size());
        model.addAttribute("savedCount", currentUser.getBookmarks().size());
        model.addAttribute("profileCompleteness", profileService.calculateCompleteness(currentUser));

        return "pages/profile";

    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal User user, @ModelAttribute("form") ProfileDTO profileDTO,RedirectAttributes redirectAttributes) {

        profileService.updateProfile(user, profileDTO);
        redirectAttributes.addFlashAttribute("saved", true);
        return "redirect:/profile";
        
    }

    @GetMapping("/profile/export")
    public void exportToPDF(HttpServletResponse response, @AuthenticationPrincipal User user) throws IOException {
        User currentUser = userRepository.findById(user.getId()).orElseThrow();

        response.setContentType("application/pdf");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String filename = "CV_" + currentUser.getLastName() + "_" + timestamp + ".pdf";

        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        profileService.exportToPdf(currentUser, response);
    }
}