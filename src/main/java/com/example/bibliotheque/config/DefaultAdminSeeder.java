package com.example.bibliotheque.config;

import com.example.bibliotheque.models.Administrator;
import com.example.bibliotheque.models.Role;
import com.example.bibliotheque.repository.AdministratorRepository;
import com.example.bibliotheque.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DefaultAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email:admin@biblio.local}")
    private String defaultAdminEmail;

    @Value("${app.default-admin.password:admin123}")
    private String defaultAdminPassword;

    public DefaultAdminSeeder(UserRepository userRepository,
                              AdministratorRepository administratorRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (defaultAdminEmail == null || defaultAdminEmail.isBlank()) {
            return;
        }

        if (userRepository.findByEmail(defaultAdminEmail).isPresent()) {
            return;
        }

        Administrator admin = new Administrator();
        admin.setFirstName("Default");
        admin.setLastName("Admin");
        admin.setEmail(defaultAdminEmail);
        admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
        admin.setRole(Role.ADMIN);
        admin.setRegistrationDate(LocalDateTime.now());
        admin.setEmployeeId("DEFAULT_ADMIN");

        administratorRepository.save(admin);
    }
}
