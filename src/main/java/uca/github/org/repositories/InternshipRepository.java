package uca.github.org.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uca.github.org.models.Internship;
import uca.github.org.models.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByStatusOrderByPublishedAtDescIdDesc(Internship.InternshipStatus status);

    List<Internship> findByPosterOrderByPublishedAtDescIdDesc(User poster);
    @Query("SELECT i FROM Internship i WHERE " +
            "i.status = 'ACTIVE' AND " +
            "(:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:sector IS NULL OR LOWER(i.sector) LIKE LOWER(CONCAT('%', :sector, '%'))) AND " +
            "(:duration IS NULL OR LOWER(i.duration) LIKE LOWER(CONCAT('%', :duration, '%'))) AND " +
            "(:company IS NULL OR LOWER(i.company) LIKE LOWER(CONCAT('%', :company, '%')))")
    List<Internship> searchOffers(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("sector") String sector,
            @Param("duration") String duration,
            @Param("company") String company
    );

}
