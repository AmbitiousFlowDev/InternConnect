package uca.github.org.services.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uca.github.org.models.Profile;
import uca.github.org.models.User;
import uca.github.org.records.ProfileDTO;
import uca.github.org.repositories.UserRepository;
import uca.github.org.services.ProfileService;

import java.awt.*;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void updateProfile(User user, ProfileDTO profileDTO) {
        User currentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.setFirstName(profileDTO.getFirstName());
        currentUser.setLastName(profileDTO.getLastName());

        Profile profile = currentUser.getProfile();
        if (profile == null) {
            profile = Profile.builder().user(currentUser).build();
            currentUser.setProfile(profile);
        }

        profile.setDescription(profileDTO.getDescription());
        profile.setEducation(profileDTO.getEducation());
        profile.setSkills(profileDTO.getSkills());
        profile.setExperience(profileDTO.getExperience());
        profile.setPreferences(profileDTO.getPreferences());

        userRepository.save(currentUser);
    }

    @Override
    public int calculateCompleteness(User user) {
        Profile profile = user.getProfile();
        int points = 20;
        if (profile == null) return points;

        if (isNotBlank(profile.getDescription())) points += 20;
        if (isNotBlank(profile.getEducation())) points += 20;
        if (isNotBlank(profile.getSkills())) points += 20;
        if (isNotBlank(profile.getExperience())) points += 20;

        return Math.min(points, 100);
    }

    @Override
    public void exportToPdf(User user, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Color primaryColor = new Color(20, 42, 110);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, primaryColor);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryColor);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);

        Paragraph name = new Paragraph(user.getFirstName() + " " + user.getLastName(), titleFont);
        name.setSpacingAfter(5);
        document.add(name);

        document.add(new Paragraph(user.getEmail(), bodyFont));
        document.add(new Paragraph("______________________________________________________________________________"));

        Profile profile = user.getProfile();
        if (profile != null) {
            addPdfSection(document, "DESCRIPTION", profile.getDescription(), sectionFont, bodyFont);
            addPdfSection(document, "FORMATION", profile.getEducation(), sectionFont, bodyFont);
            addPdfSection(document, "COMPÉTENCES", profile.getSkills(), sectionFont, bodyFont);
            addPdfSection(document, "EXPÉRIENCE", profile.getExperience(), sectionFont, bodyFont);
        }

        document.close();
    }

    private void addPdfSection(Document doc, String title, String content, Font tFont, Font bFont) throws DocumentException {
        if (isNotBlank(content)) {
            Paragraph pTitle = new Paragraph("\n" + title, tFont);
            pTitle.setSpacingBefore(10);
            doc.add(pTitle);
            doc.add(new Paragraph(content, bFont));
        }
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}