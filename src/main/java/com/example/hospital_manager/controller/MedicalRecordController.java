
package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.service.MedicalRecordService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@PreAuthorize("hasRole('DOCTOR')")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService) {

        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public MedicalRecordResponse createRecord(
            @Valid @RequestBody MedicalRecordRequest request,
            Authentication authentication) {

        return medicalRecordService.createRecord(
            request,
            authentication.getName()
        );
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordResponse> getPatientRecords(
            @PathVariable Long patientId) {

        return medicalRecordService.getPatientRecords(
            patientId
        );
    }
}
