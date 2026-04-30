package uca.github.org.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uca.github.org.models.Bookmark;
import uca.github.org.models.User;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    long countByUser(User user);
    List<Bookmark> findByUser(User user);
}
