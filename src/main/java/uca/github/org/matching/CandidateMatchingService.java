package uca.github.org.matching;

import java.util.List;

import uca.github.org.matching.dto.CandidateMatchDTO;

public interface CandidateMatchingService {

    List<CandidateMatchDTO> findMatchesForOffer(Long offerId);
}
