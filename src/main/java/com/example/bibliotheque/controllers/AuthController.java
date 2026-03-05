package com.example.bibliotheque.controllers;

import com.example.bibliotheque.dto.LoginRequest;
import com.example.bibliotheque.dto.LoginResponse;
import com.example.bibliotheque.models.User;
import com.example.bibliotheque.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.getUserByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + request.getEmail()));

            return new ResponseEntity<>(new LoginResponse(user.getEmail(), user.getRole().name()), HttpStatus.OK);
        } catch (AuthenticationException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + authentication.getName()));

        return new ResponseEntity<>(new LoginResponse(user.getEmail(), user.getRole().name()), HttpStatus.OK);
    }
}
