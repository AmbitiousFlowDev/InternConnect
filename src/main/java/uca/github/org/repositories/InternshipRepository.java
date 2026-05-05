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
}
