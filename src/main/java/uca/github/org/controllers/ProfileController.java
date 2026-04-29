package uca.github.org.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uca.github.org.models.User;
import uca.github.org.records.ProfileDTO;
import uca.github.org.repositories.UserRepository;
import uca.github.org.services.ProfileService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final ProfileService profileService;

    @GetMapping("/profile")
    public String getProfilePage(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findById(user.getId()).orElseThrow();

        model.addAttribute("user", currentUser);
        model.addAttribute("form", profileService.buildProfileForm(currentUser));
        addProfileStats(model, currentUser);

        return "pages/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @ModelAttribute("form") ProfileDTO profileDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findById(user.getId()).orElseThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUser);
            addProfileStats(model, currentUser);
            return "pages/profile";
        }

        try {
            profileService.updateProfile(currentUser, profileDTO);
            redirectAttributes.addFlashAttribute("saved", true);
            return "redirect:/profile";

        } catch (IllegalArgumentException exception) {
            model.addAttribute("user", currentUser);
            model.addAttribute("error", exception.getMessage());
            addProfileStats(model, currentUser);
            return "pages/profile";
        }
    }

    @GetMapping("/profile/export")
    public void exportToPDF(
            HttpServletResponse response,
            @AuthenticationPrincipal User user
    ) throws IOException {
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }

        User currentUser = userRepository.findById(user.getId()).orElseThrow();

        response.setContentType("application/pdf");

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String lastName = currentUser.getLastName() != null ? currentUser.getLastName() : "user";
        String filename = "CV_" + lastName + "_" + timestamp + ".pdf";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        profileService.exportToPdf(currentUser, response);
    }

    private void addProfileStats(Model model, User user) {
        model.addAttribute("applicationCount",
                user.getApplications() != null ? user.getApplications().size() : 0);

        model.addAttribute("savedCount",
                user.getBookmarks() != null ? user.getBookmarks().size() : 0);

        model.addAttribute("profileCompleteness",
                profileService.calculateCompleteness(user));
    }
}