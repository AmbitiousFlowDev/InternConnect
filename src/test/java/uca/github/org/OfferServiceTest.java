package uca.github.org;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

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
import uca.github.org.models.Bookmark;
import uca.github.org.repositories.BookmarkRepository;


@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private InternshipRepository internshipRepository;
    
    @Mock
    private BookmarkRepository bookmarkRepository;


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
    
    @Test
    void saveOffer_ShouldCreateBookmark_WhenOfferIsActiveAndNotSaved() {
        User user = User.builder()
                .id(1L)
                .role(User.Role.USER)
                .build();

        Internship offer = Internship.builder()
                .id(10L)
                .status(Internship.InternshipStatus.ACTIVE)
                .build();

        Bookmark savedBookmark = Bookmark.builder()
                .id(100L)
                .user(user)
                .internship(offer)
                .build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(bookmarkRepository.findByUserAndInternship(user, offer)).thenReturn(Optional.empty());
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(savedBookmark);

        Bookmark result = offerService.saveOffer(10L, user);

        assertSame(savedBookmark, result);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }
    
    @Test
    void saveOffer_ShouldReturnExistingBookmark_WhenAlreadySaved() {
        User user = User.builder()
                .id(1L)
                .role(User.Role.USER)
                .build();

        Internship offer = Internship.builder()
                .id(10L)
                .status(Internship.InternshipStatus.ACTIVE)
                .build();

        Bookmark existingBookmark = Bookmark.builder()
                .id(100L)
                .user(user)
                .internship(offer)
                .build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(bookmarkRepository.findByUserAndInternship(user, offer)).thenReturn(Optional.of(existingBookmark));

        Bookmark result = offerService.saveOffer(10L, user);

        assertSame(existingBookmark, result);
        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }
    
    @Test
    void removeSavedOffer_ShouldDeleteBookmark_WhenBookmarkExists() {
        User user = User.builder()
                .id(1L)
                .role(User.Role.USER)
                .build();

        Internship offer = Internship.builder()
                .id(10L)
                .status(Internship.InternshipStatus.ACTIVE)
                .build();

        Bookmark bookmark = Bookmark.builder()
                .id(100L)
                .user(user)
                .internship(offer)
                .build();

        when(internshipRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(bookmarkRepository.findByUserAndInternship(user, offer)).thenReturn(Optional.of(bookmark));

        offerService.removeSavedOffer(10L, user);

        verify(bookmarkRepository).delete(bookmark);
    }



}
