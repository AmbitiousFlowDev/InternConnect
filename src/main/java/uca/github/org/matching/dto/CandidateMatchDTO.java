package uca.github.org.matching.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateMatchDTO {

    private Long candidateId;
    private String fullName;
    private String email;
    private int score;
    private List<String> matchedCriteria;
    private String aiExplanation;
}
