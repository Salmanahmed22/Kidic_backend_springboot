package com.example.kidic.dto;

import com.example.kidic.entity.MedicalRecord;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class MedicalRecordWithFileRequestDTO {
    
    @NotNull(message = "Medical record type is required")
    private MedicalRecord.MedicalRecordType type;
    
    @NotNull(message = "Date of record is required")
    private LocalDate dateOfRecord;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private MedicalRecord.StatusType status;
    
    private MultipartFile file;
    
    // Constructors
    public MedicalRecordWithFileRequestDTO() {}
    
    public MedicalRecordWithFileRequestDTO(MedicalRecord.MedicalRecordType type, LocalDate dateOfRecord, 
                                          String description, MedicalRecord.StatusType status, MultipartFile file) {
        this.type = type;
        this.dateOfRecord = dateOfRecord;
        this.description = description;
        this.status = status;
        this.file = file;
    }
    
    // Getters and Setters
    public MedicalRecord.MedicalRecordType getType() {
        return type;
    }
    
    public void setType(MedicalRecord.MedicalRecordType type) {
        this.type = type;
    }
    
    public LocalDate getDateOfRecord() {
        return dateOfRecord;
    }
    
    public void setDateOfRecord(LocalDate dateOfRecord) {
        this.dateOfRecord = dateOfRecord;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public MedicalRecord.StatusType getStatus() {
        return status;
    }
    
    public void setStatus(MedicalRecord.StatusType status) {
        this.status = status;
    }
    
    public MultipartFile getFile() {
        return file;
    }
    
    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
