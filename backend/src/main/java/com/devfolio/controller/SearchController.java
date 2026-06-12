package com.devfolio.controller;

import com.devfolio.model.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @PersistenceContext
    private EntityManager entityManager;


    // @SuppressWarnings("unchecked") : createNativeQuery retourne une List brute sans générique
    // Java ne peut pas vérifier le type à la compilation, le cast est fait manuellement en dessous
    @SuppressWarnings("unchecked")
    @GetMapping("/projects")
    public ResponseEntity<?> searchProjects(@RequestParam String q) {

        // FIX A03 - Injection SQL
        // Avant : "... LIKE '%" + q + "%'" — q était collé directement dans la requête
        // Un attaquant pouvait envoyer ' OR '1'='1 pour récupérer tous les projets
        // Maintenant : :search est un paramètre nommé, la valeur est transmise séparément
        // La base de données reçoit la structure en premier, puis la valeur — elle ne peut plus l'interpréter comme du SQL
        String sql = "SELECT * FROM projects WHERE title LIKE :search " +
                "OR description LIKE :search";

        // Les % sont ajoutés ici en Java, pas dans la requête SQL
        // Ils font partie de la valeur transmise, pas de la structure SQL
        String param = "%" + q + "%";

        // Cast explicite nécessaire car createNativeQuery retourne une List non typée
        List<Project> results = (List<Project>) entityManager
                .createNativeQuery(sql, Project.class)
                .setParameter("search", param) // valeur injectée séparément, jamais concaténée
                .getResultList();

        return ResponseEntity.ok(results);
    }
}
