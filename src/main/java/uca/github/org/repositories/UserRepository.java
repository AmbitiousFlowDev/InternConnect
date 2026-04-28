package uca.github.org.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import uca.github.org.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    java.util.Optional<User> findByEmail(String email);
}