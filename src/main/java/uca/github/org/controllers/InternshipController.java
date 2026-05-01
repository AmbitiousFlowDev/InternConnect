package uca.github.org.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uca.github.org.services.InternshipService;

@Controller
@RequestMapping("/offers")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    @GetMapping("/search")
    public String searchOffers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) String company,
            Model model) {

        var results = internshipService.searchInternships(
                keyword, location, sector, duration, company);

        model.addAttribute("results", results);
        model.addAttribute("keyword", keyword);
        return "pages/search";
    }
}