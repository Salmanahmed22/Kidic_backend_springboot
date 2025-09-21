package com.example.kidic.controller;

import com.example.kidic.dto.MedicalRecordRequestDTO;
import com.example.kidic.dto.MedicalRecordResponseDTO;
import com.example.kidic.dto.MedicalRecordWithFileRequestDTO;
import com.example.kidic.entity.MedicalRecord;
import com.example.kidic.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(medicalRecordController).build();
    }

    // POST /api/medical-records/children/{childId}
    @Test
    void testAddMedicalRecord_Success() throws Exception {
        MedicalRecordRequestDTO request = new MedicalRecordRequestDTO();
        request.setType(MedicalRecord.MedicalRecordType.VACCINATION);
        request.setDateOfRecord(LocalDate.now());
        request.setDescription("Test vaccination");
        request.setStatus(MedicalRecord.StatusType.ACTIVE);

        MedicalRecordResponseDTO response = MedicalRecordResponseDTO.builder()
                .id(1L)
                .type(MedicalRecord.MedicalRecordType.VACCINATION)
                .dateOfRecord(LocalDate.now())
                .description("Test vaccination")
                .status(MedicalRecord.StatusType.ACTIVE)
                .childId(1L)
                .childName("Test Child")
                .build();

        when(medicalRecordService.addMedicalRecord(eq(1L), any(MedicalRecordRequestDTO.class), eq("fake-token")))
                .thenReturn(response);

        mockMvc.perform(post("/api/medical-records/children/1")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"VACCINATION\",\"dateOfRecord\":\"" + LocalDate.now() + "\",\"description\":\"Test vaccination\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test vaccination"))
                .andExpect(jsonPath("$.childId").value(1L))
                .andExpect(jsonPath("$.childName").value("Test Child"));

        verify(medicalRecordService, times(1)).addMedicalRecord(eq(1L), any(MedicalRecordRequestDTO.class), eq("fake-token"));
    }

    @Test
    void testAddMedicalRecord_MissingAuthorization() throws Exception {
        mockMvc.perform(post("/api/medical-records/children/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"VACCINATION\",\"dateOfRecord\":\"" + LocalDate.now() + "\",\"description\":\"Test vaccination\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").doesNotExist()); // No specific message in controller

        verify(medicalRecordService, never()).addMedicalRecord(anyLong(), any(MedicalRecordRequestDTO.class), anyString());
    }

    @Test
    void testAddMedicalRecord_InvalidToken() throws Exception {
        mockMvc.perform(post("/api/medical-records/children/1")
                        .header("Authorization", "InvalidToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"VACCINATION\",\"dateOfRecord\":\"" + LocalDate.now() + "\",\"description\":\"Test vaccination\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").doesNotExist());

        verify(medicalRecordService, never()).addMedicalRecord(anyLong(), any(MedicalRecordRequestDTO.class), anyString());
    }

    @Test
    void testAddMedicalRecord_NonExistentChild() throws Exception {
        when(medicalRecordService.addMedicalRecord(eq(999L), any(MedicalRecordRequestDTO.class), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Child not found"));

        mockMvc.perform(post("/api/medical-records/children/999")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"VACCINATION\",\"dateOfRecord\":\"" + LocalDate.now() + "\",\"description\":\"Test vaccination\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).addMedicalRecord(eq(999L), any(MedicalRecordRequestDTO.class), eq("fake-token"));
    }

    @Test
    void testAddMedicalRecord_InvalidRequest_MissingType() throws Exception {
        mockMvc.perform(post("/api/medical-records/children/1")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateOfRecord\":\"" + LocalDate.now() + "\",\"description\":\"Test vaccination\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).addMedicalRecord(anyLong(), any(MedicalRecordRequestDTO.class), anyString());
    }

    // GET /api/medical-records/children/{childId}
    @Test
    void testGetMedicalRecordsForChild_Success() throws Exception {
        MedicalRecordResponseDTO response1 = MedicalRecordResponseDTO.builder()
                .id(1L)
                .type(MedicalRecord.MedicalRecordType.VACCINATION)
                .dateOfRecord(LocalDate.now())
                .description("Test vaccination")
                .status(MedicalRecord.StatusType.ACTIVE)
                .childId(1L)
                .childName("Test Child")
                .build();

        MedicalRecordResponseDTO response2 = MedicalRecordResponseDTO.builder()
                .id(2L)
                .type(MedicalRecord.MedicalRecordType.CHECKUP)
                .dateOfRecord(LocalDate.now().minusDays(1))
                .description("Test checkup")
                .status(MedicalRecord.StatusType.COMPLETED)
                .childId(1L)
                .childName("Test Child")
                .build();

        List<MedicalRecordResponseDTO> responses = Arrays.asList(response1, response2);

        when(medicalRecordService.getMedicalRecordsForChild(eq(1L), eq("fake-token"))).thenReturn(responses);

        mockMvc.perform(get("/api/medical-records/children/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test vaccination"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Test checkup"));

        verify(medicalRecordService, times(1)).getMedicalRecordsForChild(eq(1L), eq("fake-token"));
    }

    @Test
    void testGetMedicalRecordsForChild_MissingAuthorization() throws Exception {
        mockMvc.perform(get("/api/medical-records/children/1"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).getMedicalRecordsForChild(anyLong(), anyString());
    }

    @Test
    void testGetMedicalRecordsForChild_NonExistentChild() throws Exception {
        when(medicalRecordService.getMedicalRecordsForChild(eq(999L), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Child not found"));

        mockMvc.perform(get("/api/medical-records/children/999")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).getMedicalRecordsForChild(eq(999L), eq("fake-token"));
    }

    // GET /api/medical-records/children/{childId}/{recordId}
    @Test
    void testGetMedicalRecord_Success() throws Exception {
        MedicalRecordResponseDTO response = MedicalRecordResponseDTO.builder()
                .id(1L)
                .type(MedicalRecord.MedicalRecordType.VACCINATION)
                .dateOfRecord(LocalDate.now())
                .description("Test vaccination")
                .status(MedicalRecord.StatusType.ACTIVE)
                .childId(1L)
                .childName("Test Child")
                .build();

        when(medicalRecordService.getMedicalRecord(eq(1L), eq(1L), eq("fake-token"))).thenReturn(response);

        mockMvc.perform(get("/api/medical-records/children/1/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test vaccination"))
                .andExpect(jsonPath("$.childId").value(1L));

        verify(medicalRecordService, times(1)).getMedicalRecord(eq(1L), eq(1L), eq("fake-token"));
    }

    @Test
    void testGetMedicalRecord_MissingAuthorization() throws Exception {
        mockMvc.perform(get("/api/medical-records/children/1/1"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).getMedicalRecord(anyLong(), anyLong(), anyString());
    }

    @Test
    void testGetMedicalRecord_NonExistentChild() throws Exception {
        when(medicalRecordService.getMedicalRecord(eq(999L), eq(1L), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Child not found"));

        mockMvc.perform(get("/api/medical-records/children/999/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).getMedicalRecord(eq(999L), eq(1L), eq("fake-token"));
    }

    @Test
    void testGetMedicalRecord_NonExistentRecord() throws Exception {
        when(medicalRecordService.getMedicalRecord(eq(1L), eq(999L), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Medical record not found"));

        mockMvc.perform(get("/api/medical-records/children/1/999")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).getMedicalRecord(eq(1L), eq(999L), eq("fake-token"));
    }

    @Test
    void testGetMedicalRecord_RecordNotBelongingToChild() throws Exception {
        when(medicalRecordService.getMedicalRecord(eq(1L), eq(1L), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Medical record does not belong to the specified child"));

        mockMvc.perform(get("/api/medical-records/children/1/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).getMedicalRecord(eq(1L), eq(1L), eq("fake-token"));
    }

    // PUT /api/medical-records/children/{childId}/{recordId}
    @Test
    void testEditMedicalRecord_Success() throws Exception {
        MedicalRecordRequestDTO request = new MedicalRecordRequestDTO();
        request.setType(MedicalRecord.MedicalRecordType.CHECKUP);
        request.setDateOfRecord(LocalDate.now().minusDays(1));
        request.setDescription("Updated checkup");
        request.setStatus(MedicalRecord.StatusType.COMPLETED);

        MedicalRecordResponseDTO response = MedicalRecordResponseDTO.builder()
                .id(1L)
                .type(MedicalRecord.MedicalRecordType.CHECKUP)
                .dateOfRecord(LocalDate.now().minusDays(1))
                .description("Updated checkup")
                .status(MedicalRecord.StatusType.COMPLETED)
                .childId(1L)
                .childName("Test Child")
                .build();

        when(medicalRecordService.editMedicalRecord(eq(1L), eq(1L), any(MedicalRecordRequestDTO.class), eq("fake-token")))
                .thenReturn(response);

        mockMvc.perform(put("/api/medical-records/children/1/1")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"CHECKUP\",\"dateOfRecord\":\"" + LocalDate.now().minusDays(1) + "\",\"description\":\"Updated checkup\",\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Updated checkup"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(medicalRecordService, times(1)).editMedicalRecord(eq(1L), eq(1L), any(MedicalRecordRequestDTO.class), eq("fake-token"));
    }

    @Test
    void testEditMedicalRecord_MissingAuthorization() throws Exception {
        mockMvc.perform(put("/api/medical-records/children/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"CHECKUP\",\"dateOfRecord\":\"" + LocalDate.now().minusDays(1) + "\",\"description\":\"Updated checkup\",\"status\":\"COMPLETED\"}"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).editMedicalRecord(anyLong(), anyLong(), any(MedicalRecordRequestDTO.class), anyString());
    }

    @Test
    void testEditMedicalRecord_NonExistentChild() throws Exception {
        when(medicalRecordService.editMedicalRecord(eq(999L), eq(1L), any(MedicalRecordRequestDTO.class), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Child not found"));

        mockMvc.perform(put("/api/medical-records/children/999/1")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"CHECKUP\",\"dateOfRecord\":\"" + LocalDate.now().minusDays(1) + "\",\"description\":\"Updated checkup\",\"status\":\"COMPLETED\"}"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).editMedicalRecord(eq(999L), eq(1L), any(MedicalRecordRequestDTO.class), eq("fake-token"));
    }

    @Test
    void testEditMedicalRecord_NonExistentRecord() throws Exception {
        when(medicalRecordService.editMedicalRecord(eq(1L), eq(999L), any(MedicalRecordRequestDTO.class), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Medical record not found"));

        mockMvc.perform(put("/api/medical-records/children/1/999")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"CHECKUP\",\"dateOfRecord\":\"" + LocalDate.now().minusDays(1) + "\",\"description\":\"Updated checkup\",\"status\":\"COMPLETED\"}"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).editMedicalRecord(eq(1L), eq(999L), any(MedicalRecordRequestDTO.class), eq("fake-token"));
    }

    @Test
    void testEditMedicalRecord_InvalidRequest_MissingDate() throws Exception {
        mockMvc.perform(put("/api/medical-records/children/1/1")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"CHECKUP\",\"description\":\"Updated checkup\",\"status\":\"COMPLETED\"}"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).editMedicalRecord(anyLong(), anyLong(), any(MedicalRecordRequestDTO.class), anyString());
    }

    // DELETE /api/medical-records/children/{childId}/{recordId}
    @Test
    void testDeleteMedicalRecord_Success() throws Exception {
        doNothing().when(medicalRecordService).deleteMedicalRecord(eq(1L), eq(1L), eq("fake-token"));

        mockMvc.perform(delete("/api/medical-records/children/1/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNoContent());

        verify(medicalRecordService, times(1)).deleteMedicalRecord(eq(1L), eq(1L), eq("fake-token"));
    }

    @Test
    void testDeleteMedicalRecord_MissingAuthorization() throws Exception {
        mockMvc.perform(delete("/api/medical-records/children/1/1"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).deleteMedicalRecord(anyLong(), anyLong(), anyString());
    }

    @Test
    void testDeleteMedicalRecord_NonExistentChild() throws Exception {
        doThrow(new IllegalArgumentException("Child not found"))
                .when(medicalRecordService).deleteMedicalRecord(eq(999L), eq(1L), eq("fake-token"));

        mockMvc.perform(delete("/api/medical-records/children/999/1")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).deleteMedicalRecord(eq(999L), eq(1L), eq("fake-token"));
    }

    @Test
    void testDeleteMedicalRecord_NonExistentRecord() throws Exception {
        doThrow(new IllegalArgumentException("Medical record not found"))
                .when(medicalRecordService).deleteMedicalRecord(eq(1L), eq(999L), eq("fake-token"));

        mockMvc.perform(delete("/api/medical-records/children/1/999")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).deleteMedicalRecord(eq(1L), eq(999L), eq("fake-token"));
    }

    @Test
    void testEditMedicalRecordWithFile_MissingAuthorization() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test file content".getBytes());

        mockMvc.perform(multipart("/api/medical-records/children/1/1/file")
                        .file(file)
                        .param("type", "VACCINATION")
                        .param("dateOfRecord", LocalDate.now().toString())
                        .param("description", "Test vaccination")
                        .param("status", "ACTIVE"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).editMedicalRecordWithFile(anyLong(), anyLong(), any(MedicalRecordWithFileRequestDTO.class), anyString());
    }

    @Test
    void testEditMedicalRecordWithFile_NonExistentChild() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test file content".getBytes());

        when(medicalRecordService.editMedicalRecordWithFile(eq(999L), eq(1L), any(MedicalRecordWithFileRequestDTO.class), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Child not found"));

        mockMvc.perform(multipart("/api/medical-records/children/999/1/file")
                        .file(file)
                        .param("type", "VACCINATION")
                        .param("dateOfRecord", LocalDate.now().toString())
                        .param("description", "Test vaccination")
                        .param("status", "ACTIVE")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).editMedicalRecordWithFile(eq(999L), eq(1L), any(MedicalRecordWithFileRequestDTO.class), eq("fake-token"));
    }

    @Test
    void testEditMedicalRecordWithFile_NonExistentRecord() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test file content".getBytes());

        when(medicalRecordService.editMedicalRecordWithFile(eq(1L), eq(999L), any(MedicalRecordWithFileRequestDTO.class), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Medical record not found"));

        mockMvc.perform(multipart("/api/medical-records/children/1/999/file")
                        .file(file)
                        .param("type", "VACCINATION")
                        .param("dateOfRecord", LocalDate.now().toString())
                        .param("description", "Test vaccination")
                        .param("status", "ACTIVE")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).editMedicalRecordWithFile(eq(1L), eq(999L), any(MedicalRecordWithFileRequestDTO.class), eq("fake-token"));
    }

    @Test
    void testEditMedicalRecordWithFile_FileUploadFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test file content".getBytes());

        when(medicalRecordService.editMedicalRecordWithFile(eq(1L), eq(1L), any(MedicalRecordWithFileRequestDTO.class), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Failed to process file upload: IO error"));

        mockMvc.perform(multipart("/api/medical-records/children/1/1/file")
                        .file(file)
                        .param("type", "VACCINATION")
                        .param("dateOfRecord", LocalDate.now().toString())
                        .param("description", "Test vaccination")
                        .param("status", "ACTIVE")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, times(1)).editMedicalRecordWithFile(eq(1L), eq(1L), any(MedicalRecordWithFileRequestDTO.class), eq("fake-token"));
    }

    @Test
    void testEditMedicalRecordWithFile_InvalidRequest_MissingType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test file content".getBytes());

        mockMvc.perform(multipart("/api/medical-records/children/1/1/file")
                        .file(file)
                        .param("dateOfRecord", LocalDate.now().toString())
                        .param("description", "Test vaccination")
                        .param("status", "ACTIVE")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).editMedicalRecordWithFile(anyLong(), anyLong(), any(MedicalRecordWithFileRequestDTO.class), anyString());
    }

    // GET /api/medical-records/children/{childId}/{recordId}/file
    @Test
    void testDownloadFile_Success() throws Exception {
        MedicalRecordResponseDTO response = MedicalRecordResponseDTO.builder()
                .id(1L)
                .type(MedicalRecord.MedicalRecordType.VACCINATION)
                .dateOfRecord(LocalDate.now())
                .description("Test vaccination")
                .fileType(MedicalRecord.FileType.PDF)
                .fileName("test.pdf")
                .fileContent("test file content".getBytes())
                .fileSize(18L)
                .fileContentType("application/pdf")
                .status(MedicalRecord.StatusType.ACTIVE)
                .childId(1L)
                .childName("Test Child")
                .build();

        when(medicalRecordService.getMedicalRecord(eq(1L), eq(1L), eq("fake-token"))).thenReturn(response);

        mockMvc.perform(get("/api/medical-records/children/1/1/file")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("application/pdf")))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.pdf\""))
                .andExpect(header().string("Content-Length", "18"))
                .andExpect(content().bytes("test file content".getBytes()));

        verify(medicalRecordService, times(1)).getMedicalRecord(eq(1L), eq(1L), eq("fake-token"));
    }

    @Test
    void testDownloadFile_MissingAuthorization() throws Exception {
        mockMvc.perform(get("/api/medical-records/children/1/1/file"))
                .andExpect(status().isBadRequest());

        verify(medicalRecordService, never()).getMedicalRecord(anyLong(), anyLong(), anyString());
    }

    @Test
    void testDownloadFile_NoFile() throws Exception {
        MedicalRecordResponseDTO response = MedicalRecordResponseDTO.builder()
                .id(1L)
                .type(MedicalRecord.MedicalRecordType.VACCINATION)
                .dateOfRecord(LocalDate.now())
                .description("Test vaccination")
                .fileContent(null)
                .status(MedicalRecord.StatusType.ACTIVE)
                .childId(1L)
                .childName("Test Child")
                .build();

        when(medicalRecordService.getMedicalRecord(eq(1L), eq(1L), eq("fake-token"))).thenReturn(response);

        mockMvc.perform(get("/api/medical-records/children/1/1/file")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).getMedicalRecord(eq(1L), eq(1L), eq("fake-token"));
    }

    @Test
    void testDownloadFile_NonExistentChild() throws Exception {
        when(medicalRecordService.getMedicalRecord(eq(999L), eq(1L), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Child not found"));

        mockMvc.perform(get("/api/medical-records/children/999/1/file")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).getMedicalRecord(eq(999L), eq(1L), eq("fake-token"));
    }

    @Test
    void testDownloadFile_NonExistentRecord() throws Exception {
        when(medicalRecordService.getMedicalRecord(eq(1L), eq(999L), eq("fake-token")))
                .thenThrow(new IllegalArgumentException("Medical record not found"));

        mockMvc.perform(get("/api/medical-records/children/1/999/file")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1)).getMedicalRecord(eq(1L), eq(999L), eq("fake-token"));
    }
}