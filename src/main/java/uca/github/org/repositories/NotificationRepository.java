package uca.github.org.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uca.github.org.models.Notification;
import uca.github.org.models.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findTop10ByUserOrderByCreatedAtDesc(User user);

    long countByUserAndIsReadFalse(User user);

    Optional<Notification> findByIdAndUser(Long id, User user);
}