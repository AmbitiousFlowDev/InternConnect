package uca.github.org.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferEditForm {
    private Long id;

    private String title;
    private String company;
    private String sector;
    private String location;
    private String duration;
    private String salary;
    private String description;

    private String requiredSkills;
    private String educationLevel;
    private String softSkills;
    private String desiredProfile;
    private String languages;

    private String requestedDocuments;
    private String expiresAt;
    private String contactEmail;
    private boolean termsAccepted;
}
