
package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.*;
import com.example.hospital_manager.model.*;
import com.example.hospital_manager.repository.*;
import com.example.hospital_manager.exception.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicalRecordService(
            MedicalRecordRepository recordRepository,
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.recordRepository = recordRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public MedicalRecordResponse createRecord(
            MedicalRecordRequest request,
            String doctorUsername) {

        AppUser doctor = userRepository
            .findByUsername(doctorUsername)
            .orElseThrow(() ->
                new ApiException(
                    "Doctor not found",
                    HttpStatus.NOT_FOUND
                )
            );

        if (doctor.getRole() != Role.DOCTOR) {

            throw new ApiException(
                "Only doctors can write medical records",
                HttpStatus.FORBIDDEN
            );
        }

        Patient patient = patientRepository
            .findById(request.patientId())
            .orElseThrow(() ->
                new ApiException(
                    "Patient not found",
                    HttpStatus.NOT_FOUND
                )
            );

        MedicalRecord record = new MedicalRecord();

        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setDiagnosis(request.diagnosis());
        record.setPrescription(request.prescription());

        record = recordRepository.save(record);

        return convertToResponse(record);
    }

    public List<MedicalRecordResponse> getPatientRecords(
            Long patientId) {

        if (!patientRepository.existsById(patientId)) {

            throw new ApiException(
                "Patient not found",
                HttpStatus.NOT_FOUND
            );
        }

        return recordRepository.findByPatientId(patientId)
            .stream()
            .map(this::convertToResponse)
            .toList();
    }

    private MedicalRecordResponse convertToResponse(
            MedicalRecord record) {

        return new MedicalRecordResponse(
            record.getId(),
            record.getPatient().getId(),
            record.getPatient().getName(),
            record.getDoctor().getUsername(),
            record.getDiagnosis(),
            record.getPrescription(),
            record.getCreatedAt()
        );
    }
}
