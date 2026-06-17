package com.devfolio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 🔴 A01-04 : ID numérique séquentiel exposé dans les URLs (IDOR)
    private Long id;

    // AJOUTÉ A01-04 : UUID généré automatiquement à la création du compte
    // Utilisé dans les URLs publiques (/profile/{uuid}) à la place de l'ID numérique
    // non devinable, non séquentiel — empêche l'énumération des profils
    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String email;

    private String password;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 50)
    private String role = "USER";

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // AJOUTÉ A01-04 : getter/setter pour le champ uuid
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}