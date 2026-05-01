package uca.github.org.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uca.github.org.models.Internship;
import uca.github.org.repositories.InternshipRepositiroy;
import uca.github.org.services.InternshipService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepositiroy internshipRepository;

    @Override
    public List<Internship> searchInternships(
            String keyword,
            String location,
            String sector,
            String duration,
            String company) {

        return internshipRepository
                .searchInternships(keyword, location, sector, duration, company);
    }
}
