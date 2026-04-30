package uca.github.org.services;

import jakarta.servlet.http.HttpServletResponse;
import uca.github.org.models.User;
import uca.github.org.records.ProfileDTO;

import java.io.IOException;

public interface ProfileService {
    ProfileDTO buildProfileForm(User user);
    void updateProfile(User authenticatedUser, ProfileDTO profileDTO);
    int calculateCompleteness(User user);
    void exportToPdf(User user, HttpServletResponse response) throws IOException;
}