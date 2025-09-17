package com.example.kidic.service;

import com.example.kidic.config.JwtService;
import com.example.kidic.dto.MedicalRecordRequestDTO;
import com.example.kidic.dto.MedicalRecordResponseDTO;
import com.example.kidic.dto.MedicalRecordWithFileRequestDTO;
import com.example.kidic.entity.Child;
import com.example.kidic.entity.MedicalRecord;
import com.example.kidic.repository.ChildRepository;
import com.example.kidic.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicalRecordService {
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private ChildRepository childRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Add a new medical record for a specific child
     */
    public MedicalRecordResponseDTO addMedicalRecord(Long childId, MedicalRecordRequestDTO request, String token) {
        // Validate that the child belongs to the family of the authenticated user
        Child child = validateChildAccess(childId, token);
        
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setType(request.getType());
        medicalRecord.setDateOfRecord(request.getDateOfRecord());
        medicalRecord.setDescription(request.getDescription());
        medicalRecord.setFileType(request.getFile());
        medicalRecord.setStatus(request.getStatus() != null ? request.getStatus() : MedicalRecord.StatusType.ACTIVE);
        medicalRecord.setChild(child);
        
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        return toResponseDTO(savedRecord);
    }
    
    /**
     * Edit an existing medical record
     */
    public MedicalRecordResponseDTO editMedicalRecord(Long childId, Long recordId, MedicalRecordRequestDTO request, String token) {
        // Validate that the child belongs to the family of the authenticated user
        validateChildAccess(childId, token);
        
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
        
        // Validate that the record belongs to the specified child
        if (!medicalRecord.getChild().getId().equals(childId)) {
            throw new IllegalArgumentException("Medical record does not belong to the specified child");
        }
        
        // Update the record
        medicalRecord.setType(request.getType());
        medicalRecord.setDateOfRecord(request.getDateOfRecord());
        medicalRecord.setDescription(request.getDescription());
        medicalRecord.setFileType(request.getFile());
        if (request.getStatus() != null) {
            medicalRecord.setStatus(request.getStatus());
        }
        
        MedicalRecord updatedRecord = medicalRecordRepository.save(medicalRecord);
        return toResponseDTO(updatedRecord);
    }
    
    /**
     * Add a new medical record with file upload for a specific child
     */
    public MedicalRecordResponseDTO addMedicalRecordWithFile(Long childId, MedicalRecordWithFileRequestDTO request, String token) throws IOException {
        // Validate that the child belongs to the family of the authenticated user
        Child child = validateChildAccess(childId, token);
        
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setType(request.getType());
        medicalRecord.setDateOfRecord(request.getDateOfRecord());
        medicalRecord.setDescription(request.getDescription());
        medicalRecord.setStatus(request.getStatus() != null ? request.getStatus() : MedicalRecord.StatusType.ACTIVE);
        medicalRecord.setChild(child);
        
        // Handle file upload if file is provided
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            FileStorageService.FileStorageResult fileResult = fileStorageService.processFileForDatabase(request.getFile());
            medicalRecord.setFileType(fileResult.getFileType());
            medicalRecord.setFileName(fileResult.getOriginalFileName());
            medicalRecord.setFileContent(fileResult.getFileContent());
            medicalRecord.setFileSize(fileResult.getFileSize());
            medicalRecord.setFileContentType(fileResult.getContentType());
        }
        
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        return toResponseDTO(savedRecord);
    }
    
    /**
     * Edit an existing medical record with optional file upload
     */
    public MedicalRecordResponseDTO editMedicalRecordWithFile(Long childId, Long recordId, MedicalRecordWithFileRequestDTO request, String token) throws IOException {
        // Validate that the child belongs to the family of the authenticated user
        validateChildAccess(childId, token);
        
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
        
        // Validate that the record belongs to the specified child
        if (!medicalRecord.getChild().getId().equals(childId)) {
            throw new IllegalArgumentException("Medical record does not belong to the specified child");
        }
        
        // Update the record
        medicalRecord.setType(request.getType());
        medicalRecord.setDateOfRecord(request.getDateOfRecord());
        medicalRecord.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            medicalRecord.setStatus(request.getStatus());
        }
        
        // Handle file upload if new file is provided
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            // Process new file for database storage
            FileStorageService.FileStorageResult fileResult = fileStorageService.processFileForDatabase(request.getFile());
            medicalRecord.setFileType(fileResult.getFileType());
            medicalRecord.setFileName(fileResult.getOriginalFileName());
            medicalRecord.setFileContent(fileResult.getFileContent());
            medicalRecord.setFileSize(fileResult.getFileSize());
            medicalRecord.setFileContentType(fileResult.getContentType());
        }
        
        MedicalRecord updatedRecord = medicalRecordRepository.save(medicalRecord);
        return toResponseDTO(updatedRecord);
    }
    
    /**
     * Delete a medical record
     */
    public void deleteMedicalRecord(Long childId, Long recordId, String token) {
        // Validate that the child belongs to the family of the authenticated user
        validateChildAccess(childId, token);
        
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
        
        // Validate that the record belongs to the specified child
        if (!medicalRecord.getChild().getId().equals(childId)) {
            throw new IllegalArgumentException("Medical record does not belong to the specified child");
        }
        
        // File content will be automatically deleted when the record is deleted from database
        
        medicalRecordRepository.delete(medicalRecord);
    }
    
    /**
     * Get all medical records for a specific child
     */
    public List<MedicalRecordResponseDTO> getMedicalRecordsForChild(Long childId, String token) {
        // Validate that the child belongs to the family of the authenticated user
        Child child = validateChildAccess(childId, token);
        
        List<MedicalRecord> records = medicalRecordRepository.findByChild(child);
        return records.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a specific medical record
     */
    public MedicalRecordResponseDTO getMedicalRecord(Long childId, Long recordId, String token) {
        // Validate that the child belongs to the family of the authenticated user
        validateChildAccess(childId, token);
        
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Medical record not found"));
        
        // Validate that the record belongs to the specified child
        if (!medicalRecord.getChild().getId().equals(childId)) {
            throw new IllegalArgumentException("Medical record does not belong to the specified child");
        }
        
        return toResponseDTO(medicalRecord);
    }
    
    /**
     * Validate that the child belongs to the family of the authenticated user
     */
    private Child validateChildAccess(Long childId, String token) {
        UUID familyId = jwtService.extractFamilyId(token);
        
        if (familyId == null) {
            throw new IllegalArgumentException("User does not belong to any family");
        }
        
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));
        
        if (!child.getFamily().getId().equals(familyId)) {
            throw new IllegalArgumentException("Child does not belong to your family");
        }
        
        return child;
    }
    
    /**
     * Convert MedicalRecord entity to MedicalRecordResponseDTO
     */
    private MedicalRecordResponseDTO toResponseDTO(MedicalRecord medicalRecord) {
        MedicalRecordResponseDTO dto = MedicalRecordResponseDTO.builder()
                .id(medicalRecord.getId())
                .type(medicalRecord.getType())
                .dateOfRecord(medicalRecord.getDateOfRecord())
                .description(medicalRecord.getDescription())
                .fileType(medicalRecord.getFileType())
                .fileName(medicalRecord.getFileName())
                .fileSize(medicalRecord.getFileSize())
                .fileContentType(medicalRecord.getFileContentType())
                .status(medicalRecord.getStatus())
                .childId(medicalRecord.getChild().getId())
                .childName(medicalRecord.getChild().getName())
                .build();
        
        // Manually set file content since Lombok builder might not handle byte[] properly
        dto.setFileContent(medicalRecord.getFileContent());
        
        return dto;
    }
}
