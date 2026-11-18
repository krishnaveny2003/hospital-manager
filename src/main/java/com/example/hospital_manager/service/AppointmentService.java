
package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import com.example.hospital_manager.exception.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public AppointmentResponse bookAppointment(
            AppointmentRequest request) {

        Patient patient = patientRepository
            .findById(request.patientId())
            .orElseThrow(() ->
                new ApiException(
                    "Patient not found",
                    HttpStatus.NOT_FOUND
                )
            );

        AppUser doctor = userRepository
            .findById(request.doctorId())
            .orElseThrow(() ->
                new ApiException(
                    "Doctor not found",
                    HttpStatus.NOT_FOUND
                )
            );

        if (doctor.getRole() != Role.DOCTOR) {

            throw new ApiException(
                "Selected user is not a doctor",
                HttpStatus.BAD_REQUEST
            );
        }

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(
            request.appointmentDate()
        );
        appointment.setStatus(
            AppointmentStatus.SCHEDULED
        );

        appointment = appointmentRepository.save(appointment);

        return convertToResponse(appointment);
    }

    public List<AppointmentResponse> getAllAppointments() {

        return appointmentRepository.findAll()
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    public List<AppointmentResponse> getDoctorAppointments(
            String username) {

        return appointmentRepository
            .findByDoctorUsername(username)
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    @Transactional
    public AppointmentResponse updateStatus(
            Long id,
            StatusRequest request,
            String doctorUsername) {

        Appointment appointment = appointmentRepository
            .findById(id)
            .orElseThrow(() ->
                new ApiException(
                    "Appointment not found",
                    HttpStatus.NOT_FOUND
                )
            );

        // Doctor ownership check
        if (!appointment.getDoctor()
                .getUsername().equals(doctorUsername)) {

            throw new ApiException(
                "You cannot update another doctor's appointment",
                HttpStatus.FORBIDDEN
            );
        }

        appointment.setStatus(request.status());

        return convertToResponse(appointment);
    }

    private AppointmentResponse convertToResponse(
            Appointment appointment) {

        return new AppointmentResponse(
            appointment.getId(),
            appointment.getPatient().getId(),
            appointment.getPatient().getName(),
            appointment.getDoctor().getId(),
            appointment.getDoctor().getUsername(),
            appointment.getAppointmentDate(),
            appointment.getStatus().name()
        );
    }
}
