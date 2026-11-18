
package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.security.JwtService;
import com.example.hospital_manager.exception.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.username())) {

            throw new ApiException(
                "Username already exists",
                HttpStatus.CONFLICT
            );
        }

        AppUser user = new AppUser(
            request.username(),
            passwordEncoder.encode(request.password()),
            request.role()
        );

        user = userRepository.save(user);

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest request) {

        AppUser user = userRepository
            .findByUsername(request.username())
            .orElseThrow(() ->
                new ApiException(
                    "Invalid username or password",
                    HttpStatus.UNAUTHORIZED
                )
            );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword())) {

            throw new ApiException(
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED
            );
        }

        String token = jwtService.generateToken(
            user.getUsername(),
            user.getRole().name()
        );

        return new AuthResponse(
            token,
            "Bearer",
            jwtService.getExpiration() / 1000,
            user.getRole().name()
        );
    }

    public UserResponse getMe(String username) {

        AppUser user = userRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new ApiException(
                    "User not found",
                    HttpStatus.NOT_FOUND
                )
            );

        return convertToResponse(user);
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    public List<UserResponse> getDoctors() {

        return userRepository.findByRole(Role.DOCTOR)
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    private UserResponse convertToResponse(AppUser user) {

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getRole().name()
        );
    }
}
