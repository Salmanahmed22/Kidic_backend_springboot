package com.example.kidic.controller;

import com.example.kidic.dto.MedicalRecordRequestDTO;
import com.example.kidic.dto.MedicalRecordResponseDTO;
import com.example.kidic.dto.MedicalRecordWithFileRequestDTO;
import com.example.kidic.service.MedicalRecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;



// ...existing code...
@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    /**
     * Add a new medical record for a specific child
     * POST /api/medical-records/children/{childId}
     */
    @PostMapping("/children/{childId}")
    public ResponseEntity<MedicalRecordResponseDTO> addMedicalRecord(
            @PathVariable Long childId,
            @Valid @RequestBody MedicalRecordRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractTokenFromHeader(authHeader);
        MedicalRecordResponseDTO response = medicalRecordService.addMedicalRecord(childId, request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all medical records for a specific child
     * GET /api/medical-records/children/{childId}
     */
    @GetMapping("/children/{childId}")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getMedicalRecordsForChild(
            @PathVariable Long childId,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractTokenFromHeader(authHeader);
        List<MedicalRecordResponseDTO> response = medicalRecordService.getMedicalRecordsForChild(childId, token);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific medical record
     * GET /api/medical-records/children/{childId}/{recordId}
     */
    @GetMapping("/children/{childId}/{recordId}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecord(
            @PathVariable Long childId,
            @PathVariable Long recordId,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractTokenFromHeader(authHeader);
        MedicalRecordResponseDTO response = medicalRecordService.getMedicalRecord(childId, recordId, token);
        return ResponseEntity.ok(response);
    }

    /**
     * Edit an existing medical record
     * PUT /api/medical-records/children/{childId}/{recordId}
     */
    @PutMapping("/children/{childId}/{recordId}")
    public ResponseEntity<MedicalRecordResponseDTO> editMedicalRecord(
            @PathVariable Long childId,
            @PathVariable Long recordId,
            @Valid @RequestBody MedicalRecordRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractTokenFromHeader(authHeader);
        MedicalRecordResponseDTO response = medicalRecordService.editMedicalRecord(childId, recordId, request, token);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a medical record
     * DELETE /api/medical-records/children/{childId}/{recordId}
     */
    @DeleteMapping("/children/{childId}/{recordId}")
    public ResponseEntity<Void> deleteMedicalRecord(
            @PathVariable Long childId,
            @PathVariable Long recordId,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractTokenFromHeader(authHeader);
        medicalRecordService.deleteMedicalRecord(childId, recordId, token);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a new medical record with file upload for a specific child
     * POST /api/medical-records/children/{childId}/with-file
     */
    @PostMapping(value = "/children/{childId}/with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicalRecordResponseDTO> addMedicalRecordWithFile(
            @PathVariable Long childId,
            @RequestParam("type") String type,
            @RequestParam("dateOfRecord") String dateOfRecord,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractTokenFromHeader(authHeader);

        try {
            MedicalRecordWithFileRequestDTO request = new MedicalRecordWithFileRequestDTO();
            request.setType(com.example.kidic.entity.MedicalRecord.MedicalRecordType.valueOf(type));
            request.setDateOfRecord(java.time.LocalDate.parse(dateOfRecord));
            request.setDescription(description);
            if (status != null) {
                request.setStatus(com.example.kidic.entity.MedicalRecord.StatusType.valueOf(status));
            }
            request.setFile(file);

            MedicalRecordResponseDTO response = medicalRecordService.addMedicalRecordWithFile(childId, request, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process file upload: " + e.getMessage());
        }
    }

    /**
     * Edit an existing medical record with optional file upload
     * PUT /api/medical-records/children/{childId}/{recordId}/with-file
     */
    @PutMapping(value = "/children/{childId}/{recordId}/with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicalRecordResponseDTO> editMedicalRecordWithFile(
            @PathVariable Long childId,
            @PathVariable Long recordId,
            @RequestParam("type") String type,
            @RequestParam("dateOfRecord") String dateOfRecord,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractTokenFromHeader(authHeader);

        try {
            MedicalRecordWithFileRequestDTO request = new MedicalRecordWithFileRequestDTO();
            request.setType(com.example.kidic.entity.MedicalRecord.MedicalRecordType.valueOf(type));
            request.setDateOfRecord(java.time.LocalDate.parse(dateOfRecord));
            request.setDescription(description);
            if (status != null) {
                request.setStatus(com.example.kidic.entity.MedicalRecord.StatusType.valueOf(status));
            }
            request.setFile(file);

            MedicalRecordResponseDTO response = medicalRecordService.editMedicalRecordWithFile(childId, recordId, request, token);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process file upload: " + e.getMessage());
        }
    }

    /**
     * Download a file associated with a medical record
     * GET /api/medical-records/children/{childId}/{recordId}/file
     */
    @GetMapping("/children/{childId}/{recordId}/file")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long childId,
            @PathVariable Long recordId,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractTokenFromHeader(authHeader);
        MedicalRecordResponseDTO record = medicalRecordService.getMedicalRecord(childId, recordId, token);

        if (record.getFileContent() == null || record.getFileContent().length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(record.getFileContentType()))
                .header("Content-Disposition", "attachment; filename=\"" + record.getFileName() + "\"")
                .header("Content-Length", String.valueOf(record.getFileSize()))
                .body(record.getFileContent());
    }

    /**
     * Extract JWT token from the Authorization header
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization token is required");
    }
}
