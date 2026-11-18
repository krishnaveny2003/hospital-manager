
package com.example.hospital_manager.config;

import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // ADMIN account
            createUser(
                userRepository,
                passwordEncoder,
                "admin",
                "admin123",
                Role.ADMIN
            );

            // DOCTOR account
            createUser(
                userRepository,
                passwordEncoder,
                "doctor1",
                "doctor123",
                Role.DOCTOR
            );

            // NURSE account
            createUser(
                userRepository,
                passwordEncoder,
                "nurse1",
                "nurse123",
                Role.NURSE
            );

            // RECEPTIONIST account
            createUser(
                userRepository,
                passwordEncoder,
                "reception1",
                "reception123",
                Role.RECEPTIONIST
            );

            // Sample patient
            if (patientRepository.count() == 0) {

                Patient patient = new Patient(
                    "John Smith",
                    "john@example.com",
                    "0771234567",
                    25
                );

                patientRepository.save(patient);
            }
        };
    }

    private void createUser(
            UserRepository repository,
            PasswordEncoder encoder,
            String username,
            String password,
            Role role) {

        if (!repository.existsByUsername(username)) {

            AppUser user = new AppUser(
                username,
                encoder.encode(password),
                role
            );

            repository.save(user);
        }
    }
}
