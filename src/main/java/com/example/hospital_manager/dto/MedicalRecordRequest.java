
package com.example.hospital_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicalRecordRequest(

    @NotNull(message = "Patient ID is required")
    Long patientId,

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 2000)
    String diagnosis,

    @NotBlank(message = "Prescription is required")
    @Size(max = 2000)
    String prescription

) {}
