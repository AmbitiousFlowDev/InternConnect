package uca.github.org.matching;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uca.github.org.matching.dto.CandidateMatchDTO;

class CandidateMatchingControllerTest {

    @Test
    void findMatchesForOffer_ShouldReturnJsonResponse() throws Exception {
        CandidateMatchingService service = org.mockito.Mockito.mock(CandidateMatchingService.class);
        CandidateMatchingController controller = new CandidateMatchingController(service);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        when(service.findMatchesForOffer(10L)).thenReturn(List.of(CandidateMatchDTO.builder()
                .candidateId(1L)
                .fullName("Mohamed Lafrouh")
                .email("mohamed@example.com")
                .score(85)
                .matchedCriteria(List.of("Skills", "Field", "Location"))
                .aiExplanation(null)
                .build()));

        mockMvc.perform(get("/api/offers/10/matches"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mohamed Lafrouh")))
                .andExpect(jsonPath("$[0].candidateId").value(1))
                .andExpect(jsonPath("$[0].score").value(85))
                .andExpect(jsonPath("$[0].matchedCriteria[0]").value("Skills"));
    }
}
