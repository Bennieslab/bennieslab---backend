package com.bennieslab.portfolio.config;

import com.bennieslab.portfolio.model.User;
import com.bennieslab.portfolio.model.enums.Role;
import com.bennieslab.portfolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class AdminInitializer {

    @Value("${admin.email:}")
    private String adminEmail;

    @Value("${admin.password:}")
    private String adminPassword;

    @Value("${admin.first-name:}")
    private String adminFirstName;

    @Value("${admin.last-name:}")
    private String adminLastName;

    @Bean
    CommandLineRunner initializeAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
                System.out.println("Admin credentials not fully configured in environment. Skipping admin sync.");
                return;
            }

            Optional<User> existingAdminOpt = userRepository.findByEmail(adminEmail);

            if (existingAdminOpt.isEmpty()) {
                User admin = new User();
                admin.setFirstName(adminFirstName);
                admin.setLastName(adminLastName);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                admin.setCareer("Computer Science Graduate");
                admin.setLocation("Livingstone, Zambia");

                userRepository.save(admin);
                System.out.println("Admin user created successfully!");
            } else {
                User admin = existingAdminOpt.get();
                boolean updated = false;

                // Sync password if environment variable has changed
                if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
                    admin.setPassword(passwordEncoder.encode(adminPassword));
                    updated = true;
                    System.out.println("Admin password updated from environment.");
                }

                // Sync first and last name if provided and changed
                if (adminFirstName != null && !adminFirstName.isBlank() && !adminFirstName.equals(admin.getFirstName())) {
                    admin.setFirstName(adminFirstName);
                    updated = true;
                }
                if (adminLastName != null && !adminLastName.isBlank() && !adminLastName.equals(admin.getLastName())) {
                    admin.setLastName(adminLastName);
                    updated = true;
                }

                // Sync career title from student to graduate
                if ("Computer Science Student".equals(admin.getCareer())) {
                    admin.setCareer("Computer Science Graduate");
                    updated = true;
                }

                if (updated) {
                    userRepository.save(admin);
                    System.out.println("Admin user synchronized with environment configuration.");
                } else {
                    System.out.println("Admin user already exists and matches environment configuration.");
                }
            }
        };
    }
}
