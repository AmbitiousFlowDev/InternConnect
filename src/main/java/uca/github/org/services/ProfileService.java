package uca.github.org.services;


import jakarta.servlet.http.HttpServletResponse;
import uca.github.org.models.User;
import uca.github.org.records.ProfileDTO;

import java.io.IOException;

public interface ProfileService {
    public void exportToPdf(User user, HttpServletResponse response) throws IOException;
    int calculateCompleteness(User user);
    void updateProfile(User user, ProfileDTO profileDTO);
}
