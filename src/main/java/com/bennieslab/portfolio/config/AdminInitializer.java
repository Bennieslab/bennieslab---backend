package com.bennieslab.portfolio.config;

import com.bennieslab.portfolio.model.User;
import com.bennieslab.portfolio.model.enums.Role;
import com.bennieslab.portfolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.first-name}")
    private String adminFirstName;

    @Value("${admin.last-name}")
    private String adminLastName;

    @Bean
    CommandLineRunner initializeAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.findByEmail(adminEmail).isEmpty()) {

                User admin = new User();
                admin.setFirstName(adminFirstName);
                admin.setLastName(adminLastName);
                admin.setEmail(adminEmail);

                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);

                admin.setCareer("Computer Science Student");
                admin.setLocation("Livingstone, Zambia");

                userRepository.save(admin);
                System.out.println("Admin user created successfully!");
            } else {
                System.out.println("Admin user already exists. Skipping initialization.");
            }
        };
    }
}