package com.example.kidic.dto;

import com.example.kidic.entity.MedicalRecord;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class MedicalRecordResponseDTO {
    
    private Long id;
    private MedicalRecord.MedicalRecordType type;
    private LocalDate dateOfRecord;
    private String description;
    private MedicalRecord.FileType fileType;
    private String fileName;
    private byte[] fileContent;
    private Long fileSize;
    private String fileContentType;
    private MedicalRecord.StatusType status;
    private Long childId;
    private String childName;
    
    public MedicalRecordResponseDTO() {}
    
    public MedicalRecordResponseDTO(Long id, MedicalRecord.MedicalRecordType type, LocalDate dateOfRecord,
                                   String description, MedicalRecord.FileType fileType, String fileName,
                                   byte[] fileContent, Long fileSize, String fileContentType,
                                   MedicalRecord.StatusType status, Long childId, String childName) {
        this.id = id;
        this.type = type;
        this.dateOfRecord = dateOfRecord;
        this.description = description;
        this.fileType = fileType;
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.fileSize = fileSize;
        this.fileContentType = fileContentType;
        this.status = status;
        this.childId = childId;
        this.childName = childName;
    }
}
