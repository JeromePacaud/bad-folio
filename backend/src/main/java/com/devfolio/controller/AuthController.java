package com.devfolio.controller;

import com.devfolio.dto.RegisterRequest;
import com.devfolio.model.User;
import com.devfolio.repository.UserRepository;
import com.devfolio.service.AuthService;
import com.devfolio.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    // 🔴 A04-01 : aucun rate limiting — brute force possible
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("email");
        String password = request.get("password");

        // 🔴 A03-04 : injection dans les logs (log injection)
        log.info("Login attempt for user: " + username);

        // 🔴 A09-01 : mot de passe loggé en clair
        log.debug("Password received: " + password);

        Optional<User> userOpt = userRepository.findByEmail(username);

        // 🔴 A04-02 : messages distincts → user enumeration
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Utilisateur inconnu"));
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 🔴 A09-02 : echec non loggé
            log.info("Wrong password for user: " + username);
            return ResponseEntity.status(401).body(Map.of("error", "Mot de passe incorrect"));
        }

        // A02-01 : migration transparente des anciens hash MD5 vers BCrypt
        if (passwordEncoder.upgradeEncoding(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token, "user", user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email déjà utilisé"));
        }

        User user = authService.register(email, password);
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token, "user", user));
    }

    // A04-04 : retourne les erreurs de validation (complexité du mot de passe, format email...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Données invalides");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<?> requestReset(@RequestParam String email) {
        String token = UUID.randomUUID().toString();
        // 🔴 A04-03 : token dans l'URL (logs serveur, historique navigateur)
        String resetUrl = "http://localhost:5173/reset-password?token=" + token + "&email=" + email;
        return ResponseEntity.ok(Map.of("resetUrl", resetUrl));
    }
}
