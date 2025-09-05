package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private MedicalRecordType type;
    
    @NotNull
    @Column(name = "date_of_record")
    private LocalDate dateOfRecord;
    
    @Size(max = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private FileType file;
    
    @Enumerated(EnumType.STRING)
    private StatusType status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;
    
    // Constructors
    public MedicalRecord() {}
    
    public MedicalRecord(MedicalRecordType type, LocalDate dateOfRecord, String description, 
                        FileType file, StatusType status, Child child) {
        this.type = type;
        this.dateOfRecord = dateOfRecord;
        this.description = description;
        this.file = file;
        this.status = status;
        this.child = child;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public MedicalRecordType getType() {
        return type;
    }
    
    public void setType(MedicalRecordType type) {
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
    
    public FileType getFile() {
        return file;
    }
    
    public void setFile(FileType file) {
        this.file = file;
    }
    
    public StatusType getStatus() {
        return status;
    }
    
    public void setStatus(StatusType status) {
        this.status = status;
    }
    
    public Child getChild() {
        return child;
    }
    
    public void setChild(Child child) {
        this.child = child;
    }
    
    public enum MedicalRecordType {
        VACCINATION, CHECKUP, ILLNESS, INJURY, ALLERGY, MEDICATION, OTHER
    }
    
    public enum FileType {
        PDF, IMAGE, DOCUMENT, VIDEO, AUDIO, OTHER
    }
    
    public enum StatusType {
        ACTIVE, ARCHIVED, PENDING, COMPLETED, CANCELLED
    }
}
