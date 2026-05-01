package uca.github.org.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uca.github.org.models.Internship;
import uca.github.org.repositories.InternshipRepository;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final InternshipRepository internshipRepository;

    @Override
    public List<Internship> getLatestInternships() {
        return internshipRepository.findByStatusOrderByPublishedAtDescIdDesc(Internship.InternshipStatus.ACTIVE);
    }
}
