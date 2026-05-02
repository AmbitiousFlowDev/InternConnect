package uca.github.org;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import uca.github.org.configuration.InternConnectSecurityConfiguration;
import uca.github.org.controllers.HomeController;
import uca.github.org.models.Internship;
import uca.github.org.records.HomeStats;
import uca.github.org.services.HomeService;

@SpringBootTest(
        classes = HomepageDisplayTest.HomepageTestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class HomepageDisplayTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HomeService homeService;

    @Test
    @DisplayName("Should display latest internship opportunities on the homepage")
    void homePage_ShouldDisplayLatestInternshipOpportunities() throws Exception {
        Internship backendInternship = Internship.builder()
                .id(1L)
                .title("Backend Developer Intern")
                .company("TechNova Solutions")
                .location("Casablanca")
                .sector("Software Development")
                .duration("3 months")
                .salary("3000 MAD/month")
                .compensation(BigDecimal.valueOf(3000))
                .requiredSkills("Java, Spring Boot, MySQL")
                .status(Internship.InternshipStatus.ACTIVE)
                .publishedAt(LocalDate.now())
                .build();

        Internship dataInternship = Internship.builder()
                .id(2L)
                .title("Data Analyst Intern")
                .company("Insight Lab")
                .location("Rabat")
                .sector("Data Analytics")
                .duration("6 months")
                .salary("3500 MAD/month")
                .compensation(BigDecimal.valueOf(3500))
                .requiredSkills("SQL, Python, Power BI")
                .status(Internship.InternshipStatus.ACTIVE)
                .publishedAt(LocalDate.now().minusDays(1))
                .build();

        when(homeService.getLatestInternships()).thenReturn(List.of(backendInternship, dataInternship));
        when(homeService.getPlatformStats()).thenReturn(new HomeStats(42, 128, 314));

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/home"))
                .andExpect(content().string(containsString("Stages publies")))
                .andExpect(content().string(containsString("42")))
                .andExpect(content().string(containsString("Utilisateurs")))
                .andExpect(content().string(containsString("128")))
                .andExpect(content().string(containsString("Candidatures soumises")))
                .andExpect(content().string(containsString("314")))
                .andExpect(content().string(containsString("Backend Developer Intern")))
                .andExpect(content().string(containsString("TechNova Solutions")))
                .andExpect(content().string(containsString("Data Analyst Intern")))
                .andExpect(content().string(containsString("Insight Lab")));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({ HomeController.class, InternConnectSecurityConfiguration.class })
    static class HomepageTestApplication {
    }
}
