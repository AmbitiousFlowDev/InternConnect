package uca.github.org.services;

import uca.github.org.models.User;

public interface AuthService {
    /**
     * Registers a new user into the system.
     * 
     * @param user The user object containing registration data.
     * @return The persisted User entity.
     */
    User register(User user);

    /**
     * Checks if an email address is already registered.
     * 
     * @param email The email to check.
     * @return true if the email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}