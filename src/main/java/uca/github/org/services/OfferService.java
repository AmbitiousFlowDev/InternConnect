package uca.github.org.services;

import uca.github.org.forms.OfferEditForm;
import uca.github.org.forms.OfferPublicationForm;
import uca.github.org.models.Internship;
import uca.github.org.models.User;

public interface OfferService {
    Internship publishOffer(OfferPublicationForm form, User poster);

    Internship updateOffer(OfferEditForm form, User currentUser);
}
