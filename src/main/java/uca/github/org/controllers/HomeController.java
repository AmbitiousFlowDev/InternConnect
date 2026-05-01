package uca.github.org.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import uca.github.org.services.HomeService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping({ "/home", "/home.html" })
    public String home(Model model) {
        model.addAttribute("internships", homeService.getLatestInternships());
        return "pages/home";
    }
}
