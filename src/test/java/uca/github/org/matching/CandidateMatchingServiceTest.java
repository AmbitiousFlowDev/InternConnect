package uca.github.org.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uca.github.org.matching.dto.CandidateMatchDTO;
import uca.github.org.models.Internship;
import uca.github.org.models.Profile;
import uca.github.org.models.User;
import uca.github.org.repositories.InternshipRepository;
import uca.github.org.repositories.ProfileRepository;

@ExtendWith(MockitoExtension.class)
class CandidateMatchingServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private CandidateMatchingServiceImpl candidateMatchingService;

    @Test
    void findMatchesForOffer_ShouldGiveHighScore_WhenSkillsMatch() {
        Internship offer = offer();
        Profile candidate = profile(1L, "Mohamed", "Lafrouh", "Java, Spring Boot, MySQL",
                "Software Development", "Master", "Casablanca", "Available for 6 months");

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(profileRepository.findAll()).thenReturn(List.of(candidate));

        List<CandidateMatchDTO> matches = candidateMatchingService.findMatchesForOffer(10L);

        assertEquals(1, matches.size());
        assertTrue(matches.get(0).getScore() >= 50);
        assertTrue(matches.get(0).getMatchedCriteria().contains("Skills"));
    }

    @Test
    void findMatchesForOffer_ShouldFilterCandidate_WhenNoCriteriaMatch() {
        Internship offer = offer();
        Profile candidate = profile(2L, "Noor", "Amrani", "Design, Figma",
                "Graphic Design", "Bachelor", "Tangier", "Part-time only");

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(profileRepository.findAll()).thenReturn(List.of(candidate));

        List<CandidateMatchDTO> matches = candidateMatchingService.findMatchesForOffer(10L);

        assertTrue(matches.isEmpty());
    }

    @Test
    void findMatchesForOffer_ShouldSortResultsByScoreDescending() {
        Internship offer = offer();
        Profile strongCandidate = profile(1L, "Amina", "Safi", "Java, Spring Boot, MySQL",
                "Software Development", "Master", "Casablanca", "Available for 6 months");
        Profile skillsOnlyCandidate = profile(2L, "Yassine", "Berrada", "Java",
                "Marketing", "Bachelor", "Rabat", "Available now");

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(profileRepository.findAll()).thenReturn(List.of(skillsOnlyCandidate, strongCandidate));

        List<CandidateMatchDTO> matches = candidateMatchingService.findMatchesForOffer(10L);

        assertFalse(matches.isEmpty());
        assertEquals(1L, matches.get(0).getCandidateId());
        assertTrue(matches.get(0).getScore() > matches.get(1).getScore());
    }

    private Internship offer() {
        return Internship.builder()
                .id(10L)
                .title("Backend Developer Intern")
                .requiredSkills("Java, Spring Boot, MySQL")
                .sector("Software Development")
                .educationLevel("Master")
                .location("Casablanca")
                .duration("6 months")
                .build();
    }

    private Profile profile(Long id, String firstName, String lastName, String skills, String sector,
                            String education, String location, String preferences) {
        User user = User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(firstName.toLowerCase() + "@example.com")
                .role(User.Role.USER)
                .build();

        return Profile.builder()
                .user(user)
                .skills(skills)
                .preferredSector(sector)
                .education(education)
                .preferredLocation(location)
                .preferences(preferences)
                .build();
    }
}
