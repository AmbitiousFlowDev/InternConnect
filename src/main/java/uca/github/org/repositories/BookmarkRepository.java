package uca.github.org.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uca.github.org.models.Bookmark;
import uca.github.org.models.User;
import uca.github.org.models.Internship;

import java.util.List;
import java.util.Optional;



@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    long countByUser(User user);
    List<Bookmark> findByUser(User user);
    
    boolean existsByUserAndInternship(User user, Internship internship);

    Optional<Bookmark> findByUserAndInternship(User user, Internship internship);

}
