package uca.github.org.matching;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uca.github.org.matching.dto.CandidateMatchDTO;
import uca.github.org.models.Internship;
import uca.github.org.models.Profile;
import uca.github.org.models.User;
import uca.github.org.repositories.InternshipRepository;
import uca.github.org.repositories.ProfileRepository;

@Service
@RequiredArgsConstructor
public class CandidateMatchingServiceImpl implements CandidateMatchingService {

    private static final Pattern SPLIT_PATTERN = Pattern.compile("[,;|/\\n\\r\\t]+");

    private final InternshipRepository internshipRepository;
    private final ProfileRepository profileRepository;

    @Override
    public List<CandidateMatchDTO> findMatchesForOffer(Long offerId) {
        Internship offer = internshipRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found."));

        return profileRepository.findAll().stream()
                .filter(profile -> isCandidate(profile.getUser()))
                .map(profile -> scoreCandidate(offer, profile))
                .filter(result -> result.getScore() >= MatchingWeights.MIN_RELEVANT_SCORE)
                .sorted(Comparator.comparingInt(CandidateMatchDTO::getScore).reversed()
                        .thenComparing(CandidateMatchDTO::getFullName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    CandidateMatchDTO scoreCandidate(Internship offer, Profile profile) {
        User candidate = profile.getUser();
        int score = 0;
        List<String> matchedCriteria = new ArrayList<>();

        if (hasSkillsMatch(offer, profile)) {
            score += MatchingWeights.SKILLS;
            matchedCriteria.add("Skills");
        }

        if (containsAny(profile.getPreferredSector(), offer.getSector())
                || containsAny(profile.getPreferences(), offer.getSector())
                || containsAny(profile.getDescription(), offer.getSector())) {
            score += MatchingWeights.FIELD;
            matchedCriteria.add("Field");
        }

        if (containsAny(profile.getEducation(), offer.getEducationLevel())) {
            score += MatchingWeights.LEVEL;
            matchedCriteria.add("Education Level");
        }

        if (locationCompatible(offer, profile)) {
            score += MatchingWeights.LOCATION;
            matchedCriteria.add("Location");
        }

        if (availabilityCompatible(offer, profile)) {
            score += MatchingWeights.AVAILABILITY;
            matchedCriteria.add("Availability");
        }

        return CandidateMatchDTO.builder()
                .candidateId(candidate.getId())
                .fullName(fullName(candidate))
                .email(candidate.getEmail())
                .score(score)
                .matchedCriteria(matchedCriteria)
                .aiExplanation(null)
                .build();
    }

    private boolean isCandidate(User user) {
        return user != null && user.getRole() == User.Role.USER;
    }

    private boolean hasSkillsMatch(Internship offer, Profile profile) {
        Set<String> offerSkills = tokens(offer.getRequiredSkills());
        if (offerSkills.isEmpty()) {
            return false;
        }

        Set<String> candidateTerms = tokens(String.join(" ",
                clean(profile.getSkills()),
                clean(profile.getExperience()),
                clean(profile.getDescription()),
                clean(profile.getPreferredKeywords())));

        return offerSkills.stream().anyMatch(candidateTerms::contains);
    }

    private boolean locationCompatible(Internship offer, Profile profile) {
        String offerLocation = clean(offer.getLocation());
        String preferredLocation = clean(profile.getPreferredLocation());
        String preferences = clean(profile.getPreferences());

        if (isRemote(offerLocation) || isRemote(preferredLocation) || isRemote(preferences)) {
            return true;
        }

        return containsAny(preferredLocation, offerLocation) || containsAny(preferences, offerLocation);
    }

    private boolean availabilityCompatible(Internship offer, Profile profile) {
        String duration = clean(offer.getDuration());
        if (duration.isBlank()) {
            return false;
        }

        return containsAny(profile.getPreferences(), duration)
                || containsAny(profile.getDescription(), duration)
                || containsAny(profile.getCoverLetter(), duration);
    }

    private boolean containsAny(String text, String expected) {
        Set<String> expectedTokens = tokens(expected);
        if (expectedTokens.isEmpty()) {
            return false;
        }

        Set<String> actualTokens = tokens(text);
        return expectedTokens.stream().anyMatch(actualTokens::contains);
    }

    private Set<String> tokens(String value) {
        Set<String> tokens = new LinkedHashSet<>();
        String normalized = normalize(value);
        if (normalized.isBlank()) {
            return tokens;
        }

        for (String part : SPLIT_PATTERN.split(normalized)) {
            String token = part.trim();
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }

        for (String token : normalized.split("\\s+")) {
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }

        return tokens;
    }

    private boolean isRemote(String value) {
        String normalized = normalize(value);
        return normalized.contains("remote")
                || normalized.contains("hybrid")
                || normalized.contains("hybride")
                || normalized.contains("distance");
    }

    private String normalize(String value) {
        String cleaned = clean(value).toLowerCase(Locale.ROOT);
        String withoutAccents = Normalizer.normalize(cleaned, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.replaceAll("[^a-z0-9+#.]+", " ").trim();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String fullName(User user) {
        String fullName = (clean(user.getFirstName()) + " " + clean(user.getLastName())).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }
}
