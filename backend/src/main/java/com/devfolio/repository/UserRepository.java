package com.devfolio.repository;

import com.devfolio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // AJOUTÉ A01-04 : recherche par UUID pour les endpoints publics
    // Evite d'exposer l'ID numérique séquentiel dans les URLs
    Optional<User> findByUuid(String uuid);
}