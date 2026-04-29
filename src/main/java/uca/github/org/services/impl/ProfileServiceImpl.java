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
        if (profile == null)
            return points;

        if (isNotBlank(profile.getDescription()))
            points += 20;
        if (isNotBlank(profile.getEducation()))
            points += 20;
        if (isNotBlank(profile.getSkills()))
            points += 20;
        if (isNotBlank(profile.getExperience()))
            points += 20;

        return Math.min(points, 100);
    }

    @Override
    public void exportToPdf(User user, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"cv-" + safeFileName(user.getFirstName()) + "-"
                        + safeFileName(user.getLastName()) + ".pdf\"");

        Document document = new Document(PageSize.A4, 50, 50, 45, 45);

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Color primaryColor = new Color(20, 42, 110);
            Color mutedColor = new Color(90, 100, 115);
            Color lineColor = new Color(220, 225, 235);

            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, primaryColor);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, mutedColor);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, primaryColor);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10.5f, Color.DARK_GRAY);

            addHeader(document, user, nameFont, subtitleFont, lineColor);

            Profile profile = user.getProfile();

            if (profile != null) {
                addPdfSection(document, "PROFIL", profile.getDescription(), sectionFont, bodyFont, lineColor);
                addPdfSection(document, "FORMATION", profile.getEducation(), sectionFont, bodyFont, lineColor);
                addPdfSection(document, "COMPÉTENCES", profile.getSkills(), sectionFont, bodyFont, lineColor);
                addPdfSection(document, "EXPÉRIENCE", profile.getExperience(), sectionFont, bodyFont, lineColor);
                addPdfSection(document, "PRÉFÉRENCES", profile.getPreferences(), sectionFont, bodyFont, lineColor);
            } else {
                Paragraph empty = new Paragraph("Aucune information de profil disponible.", bodyFont);
                empty.setSpacingBefore(20);
                document.add(empty);
            }

        } catch (DocumentException e) {
            throw new IOException("Erreur lors de la génération du PDF", e);
        } finally {
            document.close();
        }
    }

    private void addHeader(Document document, User user, Font nameFont, Font subtitleFont, Color lineColor)
            throws DocumentException {

        String fullName = joinNonBlank(user.getFirstName(), user.getLastName());
        if (!isNotBlank(fullName)) {
            fullName = "Curriculum Vitae";
        }

        Paragraph name = new Paragraph(fullName, nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingAfter(6);
        document.add(name);

        Paragraph email = new Paragraph(nullSafe(user.getEmail()), subtitleFont);
        email.setAlignment(Element.ALIGN_CENTER);
        email.setSpacingAfter(18);
        document.add(email);

        addSeparator(document, lineColor);
    }

    private void addPdfSection(
            Document document,
            String title,
            String content,
            Font titleFont,
            Font bodyFont,
            Color lineColor) throws DocumentException {

        if (!isNotBlank(content)) {
            return;
        }

        Paragraph sectionTitle = new Paragraph(title, titleFont);
        sectionTitle.setSpacingBefore(18);
        sectionTitle.setSpacingAfter(6);
        document.add(sectionTitle);

        addSeparator(document, lineColor);

        Paragraph body = new Paragraph(content.trim(), bodyFont);
        body.setLeading(15);
        body.setSpacingBefore(8);
        body.setSpacingAfter(4);
        body.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(body);
    }

    private void addSeparator(Document document, Color lineColor) throws DocumentException {
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(lineColor);
        cell.setFixedHeight(6);
        cell.setPadding(0);

        line.addCell(cell);
        document.add(line);
    }

    private String joinNonBlank(String first, String second) {
        StringBuilder builder = new StringBuilder();

        if (isNotBlank(first)) {
            builder.append(first.trim());
        }

        if (isNotBlank(second)) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(second.trim());
        }

        return builder.toString();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeFileName(String value) {
        if (value == null || value.isBlank()) {
            return "user";
        }

        return value
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9-_]", "-")
                .replaceAll("-+", "-");
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

}