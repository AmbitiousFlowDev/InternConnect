package uca.github.org.services;

import org.springframework.stereotype.Service;
import uca.github.org.models.User;

import java.util.Locale;

@Service
public class UserDisplayService {

    public String getDisplayName(User user) {
        if (user == null) {
            return "Utilisateur";
        }

        String firstName = clean(user.getFirstName());
        String lastName = clean(user.getLastName());
        String fullName = (firstName + " " + lastName).trim();
        if (!fullName.isBlank()) {
            return fullName;
        }

        String username = clean(user.getUsername());
        return username.isBlank() ? "Utilisateur" : username;
    }

    public String getInitials(User user) {
        if (user == null) {
            return "U";
        }

        String firstName = clean(user.getFirstName());
        String lastName = clean(user.getLastName());
        if (!firstName.isBlank() && !lastName.isBlank()) {
            return (firstName.substring(0, 1) + lastName.substring(0, 1)).toUpperCase(Locale.ROOT);
        }

        String username = clean(user.getUsername());
        if (!username.isBlank()) {
            return username.substring(0, Math.min(2, username.length())).toUpperCase(Locale.ROOT);
        }

        return "U";
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
