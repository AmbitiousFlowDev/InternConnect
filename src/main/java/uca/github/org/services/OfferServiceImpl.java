package uca.github.org.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uca.github.org.forms.OfferEditForm;
import uca.github.org.forms.OfferPublicationForm;
import uca.github.org.models.Bookmark;
import uca.github.org.models.Internship;
import uca.github.org.models.User;
import uca.github.org.repositories.BookmarkRepository;
import uca.github.org.repositories.InternshipRepository;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final InternshipRepository internshipRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public Internship publishOffer(OfferPublicationForm form, User poster) {
        Internship internship = Internship.builder()
                .poster(poster)
                .title(form.getTitle())
                .company(form.getCompany())
                .sector(form.getSector())
                .location(form.getLocation())
                .duration(form.getDuration())
                .salary(form.getSalary())
                .description(form.getDescription())
                .status(Internship.InternshipStatus.ACTIVE)
                .publishedAt(LocalDate.now())
                .requiredSkills(form.getRequiredSkills())
                .educationLevel(form.getEducationLevel())
                .softSkills(form.getSoftSkills())
                .desiredProfile(form.getDesiredProfile())
                .languages(form.getLanguages())
                .requestedDocuments(form.getRequestedDocuments())
                .contactEmail(form.getContactEmail())
                .expiresAt(form.getExpiresAt() != null && !form.getExpiresAt().isBlank()
                        ? LocalDate.parse(form.getExpiresAt())
                        : null)
                .build();

        return internshipRepository.save(internship);
    }

    @Override
    public Internship updateOffer(OfferEditForm form, User currentUser) {
        Internship offer = internshipRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Offre introuvable."));

        if (!offer.getPoster().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("Vous n'avez pas le droit de modifier cette offre.");
        }

        offer.setTitle(form.getTitle());
        offer.setCompany(form.getCompany());
        offer.setSector(form.getSector());
        offer.setLocation(form.getLocation());
        offer.setDuration(form.getDuration());
        offer.setSalary(form.getSalary());
        offer.setDescription(form.getDescription());

        offer.setRequiredSkills(form.getRequiredSkills());
        offer.setEducationLevel(form.getEducationLevel());
        offer.setSoftSkills(form.getSoftSkills());
        offer.setDesiredProfile(form.getDesiredProfile());
        offer.setLanguages(form.getLanguages());

        offer.setRequestedDocuments(form.getRequestedDocuments());
        offer.setContactEmail(form.getContactEmail());

        if (form.getExpiresAt() != null && !form.getExpiresAt().isBlank()) {
            offer.setExpiresAt(LocalDate.parse(form.getExpiresAt()));
        }

        return internshipRepository.save(offer);
    }

    @Override
    public void deleteOffer(Long id, User currentUser) {
        Internship offer = internshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Offre introuvable."));

        if (!offer.getPoster().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("Vous n'avez pas le droit de supprimer cette offre.");
        }

        offer.setStatus(Internship.InternshipStatus.ARCHIVED);
        internshipRepository.save(offer);
    }

    @Override
    public List<Bookmark> getSavedOffers(User currentUser) {
        return bookmarkRepository.findByUserOrderByAddedAtDesc(currentUser);
    }

    @Override
    public Bookmark saveOffer(Long offerId, User currentUser) {
        Internship offer = internshipRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offre introuvable."));

        if (offer.getStatus() != Internship.InternshipStatus.ACTIVE) {
            throw new IllegalArgumentException("Cette offre n'est plus disponible.");
        }

        return bookmarkRepository.findByUserAndInternship(currentUser, offer)
                .orElseGet(() -> bookmarkRepository.save(
                        Bookmark.builder()
                                .user(currentUser)
                                .internship(offer)
                                .build()
                ));
    }

    @Override
    public void removeSavedOffer(Long offerId, User currentUser) {
        Internship offer = internshipRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offre introuvable."));

        Bookmark bookmark = bookmarkRepository.findByUserAndInternship(currentUser, offer)
                .orElseThrow(() -> new IllegalArgumentException("Cette offre n'est pas sauvegardée."));

        bookmarkRepository.delete(bookmark);
    }

    @Override
    public List<Internship> searchOffers(
            String keyword,
            String location,
            String sector,
            String duration,
            String company) {

        return internshipRepository.searchOffers(
                keyword,
                location,
                sector,
                duration,
                company
        );
    }
}