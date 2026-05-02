package uca.github.org.services;

import java.util.List;

import uca.github.org.models.Internship;
import uca.github.org.records.HomeStats;

public interface HomeService {
    List<Internship> getLatestInternships();
    HomeStats getPlatformStats();
}
