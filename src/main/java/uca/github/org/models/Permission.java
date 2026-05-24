package uca.github.org.models;

public enum Permission {
    MANAGE_ROLES("Gestion des roles", "Modifier les permissions associees aux roles."),
    ASSIGN_ROLES("Affectation des roles", "Attribuer des roles aux utilisateurs."),
    VIEW_USERS("Consultation utilisateurs", "Consulter les utilisateurs inscrits."),
    PUBLISH_OFFERS("Publication d'offres", "Publier de nouvelles offres de stage."),
    MANAGE_OWN_OFFERS("Gestion de ses offres", "Modifier et suivre ses propres offres."),
    MANAGE_ANY_OFFER("Gestion globale des offres", "Administrer toutes les offres publiees."),
    VIEW_OFFER_APPLICATIONS("Consultation candidatures", "Voir les candidatures liees aux offres."),
    APPLY_TO_OFFERS("Candidature aux offres", "Postuler aux offres disponibles.");

    private final String label;
    private final String description;

    Permission(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
