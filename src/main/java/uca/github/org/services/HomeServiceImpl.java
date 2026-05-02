package uca.github.org.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uca.github.org.models.Internship;
import uca.github.org.records.HomeStats;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.repositories.InternshipRepository;
import uca.github.org.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public List<Internship> getLatestInternships() {
        return internshipRepository.findByStatusOrderByPublishedAtDescIdDesc(Internship.InternshipStatus.ACTIVE);
    }

    @Override
    public HomeStats getPlatformStats() {
        return new HomeStats(
                internshipRepository.count(),
                userRepository.count(),
                applicationRepository.count());
    }
}
