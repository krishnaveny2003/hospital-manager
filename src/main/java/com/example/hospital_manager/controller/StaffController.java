
package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StaffController {

    private final AuthService authService;

    public StaffController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {

        return authService.getAllUsers();
    }

    @GetMapping("/api/doctors")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<UserResponse> getDoctors() {

        return authService.getDoctors();
    }

    @PostMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse registerStaff(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }
}
