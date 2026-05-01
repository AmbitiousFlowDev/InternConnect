package uca.github.org.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uca.github.org.models.Internship;

import java.util.List;
import java.util.UUID;

public interface InternshipRepositiroy extends JpaRepository<Internship, UUID> {
    @Query("SELECT i FROM Internship i WHERE " +
            "(:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:sector IS NULL OR LOWER(i.sector) LIKE LOWER(CONCAT('%', :sector, '%'))) AND " +
            "(:duration IS NULL OR LOWER(i.duration) LIKE LOWER(CONCAT('%', :duration, '%'))) AND " +
            "(:company IS NULL OR LOWER(i.company) LIKE LOWER(CONCAT('%', :company, '%'))) AND " +
            "i.status = 'ACTIVE'")
    List<Internship> searchInternships(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("sector") String sector,
            @Param("duration") String duration,
            @Param("company") String company
    );
}
