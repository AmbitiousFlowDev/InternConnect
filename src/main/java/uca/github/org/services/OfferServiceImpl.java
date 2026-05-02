package uca.github.org.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uca.github.org.forms.OfferPublicationForm;
import uca.github.org.models.Internship;
import uca.github.org.models.User;
import uca.github.org.repositories.InternshipRepository;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final InternshipRepository internshipRepository;

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
                .build();

        return internshipRepository.save(internship);
    }
}
