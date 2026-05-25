package uca.github.org.matching;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uca.github.org.matching.dto.CandidateMatchDTO;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class CandidateMatchingController {

    private final CandidateMatchingService candidateMatchingService;

    @GetMapping("/{offerId}/matches")
    @PreAuthorize("hasAnyRole('POSTER', 'RECRUITER', 'ADMIN')")
    public List<CandidateMatchDTO> findMatchesForOffer(@PathVariable Long offerId) {
        return candidateMatchingService.findMatchesForOffer(offerId);
    }
}
