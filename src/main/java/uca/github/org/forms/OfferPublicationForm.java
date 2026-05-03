package uca.github.org.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferPublicationForm {

    @NotBlank(message = "Le titre du stage est obligatoire.")
    @Size(max = 200, message = "Le titre ne doit pas dépasser 200 caractères.")
    private String title;

    @NotBlank(message = "Le nom de l'entreprise est obligatoire.")
    private String company;

    @NotBlank(message = "Le secteur est obligatoire.")
    private String sector;

    @NotBlank(message = "La localisation est obligatoire.")
    private String location;

    @NotBlank(message = "La durée est obligatoire.")
    private String duration;

    private String salary;

    @NotBlank(message = "La description est obligatoire.")
    @Size(min = 20, message = "La description doit contenir au moins 20 caractères.")
    private String description;
    
    @NotBlank(message = "Les compétences techniques sont obligatoires.")
    private String requiredSkills;

    @NotBlank(message = "Le niveau d'études requis est obligatoire.")
    private String educationLevel;

    private String softSkills;

    @NotBlank(message = "Le profil recherché est obligatoire.")
    private String desiredProfile;

    private String languages;
    
    @NotBlank(message = "Les documents demandés sont obligatoires.")
    private String requestedDocuments;

    @NotBlank(message = "La date d'expiration est obligatoire.")
    private String expiresAt;

    @NotBlank(message = "L'email de contact est obligatoire.")
    @Email(message = "L'email de contact doit être valide.")
    private String contactEmail;

    @AssertTrue(message = "Vous devez confirmer l'exactitude des informations.")
    private boolean termsAccepted;



}
