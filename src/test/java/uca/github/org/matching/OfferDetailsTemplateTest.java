package uca.github.org.matching;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class OfferDetailsTemplateTest {

    @Test
    void offerDetailsTemplate_ShouldDisplayCandidateMatchingSection() throws Exception {
        String template = Files.readString(Path.of("src/main/resources/templates/pages/offers/details.html"));

        assertTrue(template.contains("Candidats recommandés"));
        assertTrue(template.contains("Aucun candidat pertinent trouvé pour cette offre."));
        assertTrue(template.contains("Critères correspondants"));
    }
}
