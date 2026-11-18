
package com.example.hospital_manager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record AppointmentRequest(

    @NotNull(message = "Patient ID is required")
    Long patientId,

    @NotNull(message = "Doctor ID is required")
    Long doctorId,

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    LocalDateTime appointmentDate

) {}
