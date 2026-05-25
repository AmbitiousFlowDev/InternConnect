package uca.github.org.matching;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class OfferDetailsTemplateTest {

    @Test
    void offerDetailsTemplate_ShouldDisplayCandidateMatchingSection() throws Exception {
        String template = Files.readString(Path.of("src/main/resources/templates/pages/offers/details.html"));

        assertTrue(template.contains("Recommended Candidates"));
        assertTrue(template.contains("No relevant candidates found for this offer."));
        assertTrue(template.contains("Matched Criteria"));
    }
}
