
package com.example.hospital_manager.service;

import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.MedicalRecordRepository;
import com.example.hospital_manager.exception.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            MedicalRecordRepository medicalRecordRepository) {

        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public Patient createPatient(Patient patient) {

        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {

        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {

        return patientRepository.findById(id)
            .orElseThrow(() ->
                new ApiException(
                    "Patient not found",
                    HttpStatus.NOT_FOUND
                )
            );
    }

    public List<Patient> searchPatients(String name) {

        return patientRepository
            .findByNameContainingIgnoreCase(name);
    }

    public Patient updatePatient(
            Long id,
            Patient updatedPatient) {

        Patient patient = getPatientById(id);

        patient.setName(updatedPatient.getName());
        patient.setEmail(updatedPatient.getEmail());
        patient.setPhone(updatedPatient.getPhone());
        patient.setAge(updatedPatient.getAge());

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {

        Patient patient = getPatientById(id);

        boolean hasAppointments =
            appointmentRepository.existsByPatientId(id);

        boolean hasMedicalRecords =
            medicalRecordRepository.existsByPatientId(id);

        if (hasAppointments || hasMedicalRecords) {

            throw new ApiException(
                "Cannot delete patient because patient has appointments or medical records",
                HttpStatus.CONFLICT
            );
        }

        patientRepository.delete(patient);
    }
}
