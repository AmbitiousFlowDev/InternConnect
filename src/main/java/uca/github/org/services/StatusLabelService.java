package uca.github.org.services;

import org.springframework.stereotype.Service;
import uca.github.org.models.Application;
import uca.github.org.models.RecruiterVerification;

@Service
public class StatusLabelService {

    public String application(Application.ApplicationStatus status) {
        if (status == null) {
            return "Toutes";
        }
        return switch (status) {
            case SUBMITTED -> "Soumise";
            case UNDER_REVIEW -> "En cours d'examen";
            case ACCEPTED -> "Acceptée";
            case REJECTED -> "Refusée";
            case WITHDRAWN -> "Retirée";
        };
    }

    public String applicationFilter(String status) {
        if (status == null || status.equals("ALL")) {
            return "Toutes";
        }
        return switch (status) {
            case "SUBMITTED" -> "Soumises";
            case "UNDER_REVIEW" -> "En cours d'examen";
            case "ACCEPTED" -> "Acceptées";
            case "REJECTED" -> "Refusées";
            default -> "Toutes";
        };
    }

    public String verification(RecruiterVerification.VerificationStatus status) {
        if (status == null) {
            return "Non soumise";
        }
        return switch (status) {
            case PENDING -> "En attente";
            case APPROVED -> "Approuvé";
            case REJECTED -> "Refusé";
        };
    }
}
