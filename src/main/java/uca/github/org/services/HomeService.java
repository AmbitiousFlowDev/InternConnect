package uca.github.org.services;

import java.util.List;

import uca.github.org.models.Internship;

public interface HomeService {
    List<Internship> getLatestInternships();
}
