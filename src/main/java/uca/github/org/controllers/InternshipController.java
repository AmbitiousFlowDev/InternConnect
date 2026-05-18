package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import uca.github.org.models.User;
import uca.github.org.repositories.InternshipRepository;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipRepository internshipRepository;

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
}