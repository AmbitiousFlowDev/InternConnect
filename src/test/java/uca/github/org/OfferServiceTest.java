package uca.github.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uca.github.org.models.Internship;
import uca.github.org.models.User;
import uca.github.org.repositories.InternshipRepository;
import uca.github.org.services.OfferServiceImpl;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @InjectMocks
    private OfferServiceImpl offerService;

    @Test
    void deleteOffer_ShouldArchiveOffer_WhenPosterOwnsOffer() {
        User poster = User.builder()
                .id(1L)
                .role(User.Role.POSTER)
                .build();

        Internship offer = Internship.builder()
                .id(10L)
                .poster(poster)
                .status(Internship.InternshipStatus.ACTIVE)
                .build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(offer));

        offerService.deleteOffer(10L, poster);

        assertEquals(Internship.InternshipStatus.ARCHIVED, offer.getStatus());
        verify(internshipRepository).save(offer);
    }
}
