package com.example.kidic.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileStorageService {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    private static final String[] ALLOWED_EXTENSIONS = {
        ".pdf", ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff",
        ".doc", ".docx", ".txt", ".mp4", ".avi", ".mov", ".mp3", ".wav"
    };
    
    private static final String[] ALLOWED_CONTENT_TYPES = {
        "application/pdf",
        "image/jpeg", "image/png", "image/gif", "image/bmp", "image/tiff",
        "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain", "video/mp4", "video/avi", "video/quicktime",
        "audio/mpeg", "audio/wav"
    };
    
    /**
     * Process uploaded file and return file information for database storage
     */
    public FileStorageResult processFileForDatabase(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
        
        // Validate file extension
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !isValidFileExtension(originalFileName)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: PDF, Images, Documents, Videos, Audio");
        }
        
        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !isValidContentType(contentType)) {
            throw new IllegalArgumentException("File content type not allowed");
        }
        
        // Read file content into byte array
        byte[] fileContent = file.getBytes();
        
        // Return file information for database storage
        return new FileStorageResult(
            originalFileName,
            fileContent,
            file.getSize(),
            contentType,
            getFileTypeFromContentType(contentType)
        );
    }
    
    /**
     * Validate file for upload
     */
    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
        
        // Validate file extension
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !isValidFileExtension(originalFileName)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: PDF, Images, Documents, Videos, Audio");
        }
        
        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !isValidContentType(contentType)) {
            throw new IllegalArgumentException("File content type not allowed");
        }
    }
    
    private boolean isValidFileExtension(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        for (String allowedExtension : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExtension)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidContentType(String contentType) {
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }
        return false;
    }
    
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
    
    private com.example.kidic.entity.MedicalRecord.FileType getFileTypeFromContentType(String contentType) {
        if (contentType.startsWith("image/")) {
            return com.example.kidic.entity.MedicalRecord.FileType.IMAGE;
        } else if (contentType.equals("application/pdf")) {
            return com.example.kidic.entity.MedicalRecord.FileType.PDF;
        } else if (contentType.startsWith("video/")) {
            return com.example.kidic.entity.MedicalRecord.FileType.VIDEO;
        } else if (contentType.startsWith("audio/")) {
            return com.example.kidic.entity.MedicalRecord.FileType.AUDIO;
        } else if (contentType.contains("document") || contentType.contains("word") || contentType.equals("text/plain")) {
            return com.example.kidic.entity.MedicalRecord.FileType.DOCUMENT;
        } else {
            return com.example.kidic.entity.MedicalRecord.FileType.OTHER;
        }
    }
    
    /**
     * Result class for file processing operations
     */
    public static class FileStorageResult {
        private final String originalFileName;
        private final byte[] fileContent;
        private final long fileSize;
        private final String contentType;
        private final com.example.kidic.entity.MedicalRecord.FileType fileType;
        
        public FileStorageResult(String originalFileName, byte[] fileContent,
                               long fileSize, String contentType, com.example.kidic.entity.MedicalRecord.FileType fileType) {
            this.originalFileName = originalFileName;
            this.fileContent = fileContent;
            this.fileSize = fileSize;
            this.contentType = contentType;
            this.fileType = fileType;
        }
        
        public String getOriginalFileName() {
            return originalFileName;
        }
        
        public byte[] getFileContent() {
            return fileContent;
        }
        
        public long getFileSize() {
            return fileSize;
        }
        
        public String getContentType() {
            return contentType;
        }
        
        public com.example.kidic.entity.MedicalRecord.FileType getFileType() {
            return fileType;
        }
    }
}
