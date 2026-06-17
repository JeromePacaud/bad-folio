package com.devfolio.controller;

import com.devfolio.dto.UserUpdateRequest;
import com.devfolio.model.User;
import com.devfolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    // 🔴 A01-03 : endpoint admin sans vérification de rôle ADMIN
    // Retourne les hashes MD5 des mots de passe
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // AJOUTÉ A01-04 : endpoint public par UUID à la place de l'ID numérique
    // /users/profile/{uuid} — non devinable, empêche l'énumération des profils
    @GetMapping("/users/profile/{uuid}")
    public ResponseEntity<User> getUserByUuid(@PathVariable String uuid) {
        return userRepository.findByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // 🔴 A01-04 : modification de n'importe quel profil sans contrôle d'identité
    // Correction : ajout de @PreAuthorize avec comme role = ADMIN
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest dto) {
        return userRepository.findById(id).map(user -> {
            user.setEmail(dto.getEmail());
            user.setBio(dto.getBio());
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }
}