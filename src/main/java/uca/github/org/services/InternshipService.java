package uca.github.org.services;

import uca.github.org.models.Internship;
import java.util.List;

public interface InternshipService {
    List<Internship> searchInternships(
            String keyword,
            String location,
            String sector,
            String duration,
            String company
    );
}