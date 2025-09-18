package com.example.kidic.entity;

import jakarta.persistence.*;
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
    @Column(name = "file_type")
    private FileType fileType;
    
    @Size(max = 500)
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_content", columnDefinition = "LONGBLOB")
    private byte[] fileContent;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Size(max = 100)
    @Column(name = "file_content_type")
    private String fileContentType;
    
    @Enumerated(EnumType.STRING)
    private StatusType status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;
    
    // Constructors
    public MedicalRecord() {}
    
    public MedicalRecord(MedicalRecordType type, LocalDate dateOfRecord, String description, 
                        FileType fileType, StatusType status, Child child) {
        this.type = type;
        this.dateOfRecord = dateOfRecord;
        this.description = description;
        this.fileType = fileType;
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
    
    public FileType getFileType() {
        return fileType;
    }
    
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public byte[] getFileContent() {
        return fileContent;
    }
    
    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getFileContentType() {
        return fileContentType;
    }
    
    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
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
