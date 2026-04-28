package uca.github.org.dto;

import uca.github.org.models.Profile;
import uca.github.org.models.User;

public record ProfileUpdateForm(
        String firstName,
        String lastName,
        String description,
        String skills,
        String experience,
        String education,
        String preferences,
        String coverLetter,
        String resumeFile
) {
    public static ProfileUpdateForm from(User user, Profile profile) {
        return new ProfileUpdateForm(
                user != null ? user.getFirstName() : "",
                user != null ? user.getLastName() : "",
                profile != null ? profile.getDescription() : "",
                profile != null ? profile.getSkills() : "",
                profile != null ? profile.getExperience() : "",
                profile != null ? profile.getEducation() : "",
                profile != null ? profile.getPreferences() : "",
                profile != null ? profile.getCoverLetter() : "",
                profile != null ? profile.getResumeFile() : ""
        );
    }
}

