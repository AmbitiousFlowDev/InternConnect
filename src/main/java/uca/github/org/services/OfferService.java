package uca.github.org.services;

import java.util.List;

import uca.github.org.forms.OfferEditForm;
import uca.github.org.forms.OfferPublicationForm;
import uca.github.org.models.Bookmark;
import uca.github.org.models.Internship;
import uca.github.org.models.User;

public interface OfferService {

    Internship publishOffer(OfferPublicationForm form, User poster);

    Internship updateOffer(OfferEditForm form, User currentUser);

    void deleteOffer(Long id, User currentUser);

    List<Bookmark> getSavedOffers(User currentUser);

    Bookmark saveOffer(Long offerId, User currentUser);

    void removeSavedOffer(Long offerId, User currentUser);

    List<Internship> searchOffers(
            String keyword,
            String location,
            String sector,
            String duration,
            String company
    );
}