package uca.github.org.repositories;

import uca.github.org.models.Recommendation;
import uca.github.org.models.User;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation , Long> {
    List<Recommendation> findByUserOrderByScoreDesc(User user); 
    long countByUser(User user);
}
