
package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorUsername(
        String username
    );

    boolean existsByPatientId(Long patientId);

    List<Appointment> findByPatientId(Long patientId);
}
