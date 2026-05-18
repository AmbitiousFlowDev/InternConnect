package uca.github.org.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uca.github.org.models.Internship;
import uca.github.org.models.User;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByStatusOrderByPublishedAtDescIdDesc(
            Internship.InternshipStatus status
    );

    List<Internship> findByPosterAndStatusNotOrderByPublishedAtDescIdDesc(
            User poster,
            Internship.InternshipStatus status
    );

    @Query("""
        SELECT i FROM Internship i
        WHERE
        (:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        
        AND (:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%')))
        
        AND (:sector IS NULL OR LOWER(i.sector) LIKE LOWER(CONCAT('%', :sector, '%')))
        
        AND (:duration IS NULL OR LOWER(i.duration) LIKE LOWER(CONCAT('%', :duration, '%')))
        
        AND (:status IS NULL OR LOWER(CAST(i.status as string)) LIKE LOWER(CONCAT('%', :status, '%')))
        """)
    List<Internship> searchOffers(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("sector") String sector,
            @Param("duration") String duration,
            @Param("status") String status
    );
}