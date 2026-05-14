package uca.github.org.services;

import uca.github.org.forms.OfferEditForm;
import uca.github.org.forms.OfferPublicationForm;
import uca.github.org.models.Internship;
import uca.github.org.models.User;
import java.util.List;

public interface OfferService {
    Internship publishOffer(OfferPublicationForm form, User poster);

    Internship updateOffer(OfferEditForm form, User currentUser);
    
    void deleteOffer(Long id, User currentUser);

    List<Internship> searchOffers(
            String keyword,
            String location,
            String sector,
            String duration,
            String company
    );
}
