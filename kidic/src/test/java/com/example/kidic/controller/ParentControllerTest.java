package com.example.kidic.controller;

import com.example.kidic.dto.ParentResponseDTO;
import com.example.kidic.dto.ParentUpdateRequestDTO;
import com.example.kidic.entity.Parent;
import com.example.kidic.service.ParentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ParentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParentService parentService;

    @InjectMocks
    private ParentController parentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parentController).build();
    }

    // GET /api/parent
    @Test
    void testGetParent_Success() throws Exception {
        ParentResponseDTO response = new ParentResponseDTO();
        response.setId(1L);
        response.setName("John Smith");
        response.setPhone("1234567890");
        response.setEmail("john.smith@email.com");
        response.setGender(true);
        response.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);

        when(parentService.getParent(eq("fake-token"))).thenReturn(response);

        mockMvc.perform(get("/api/parent")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.email").value("john.smith@email.com"))
                .andExpect(jsonPath("$.gender").value(true))
                .andExpect(jsonPath("$.profilePictureType").value("DEFAULT"));

        verify(parentService, times(1)).getParent(eq("fake-token"));
    }

    @Test
    void testGetParent_MissingAuthorization() throws Exception {
        mockMvc.perform(get("/api/parent"))
                .andExpect(status().isBadRequest());

        verify(parentService, never()).getParent(anyString());
    }

    @Test
    void testGetParent_InvalidAuthorization() throws Exception {
        mockMvc.perform(get("/api/parent")
                        .header("Authorization", "InvalidHeader"))
                .andExpect(status().isBadRequest());

        verify(parentService, never()).getParent(anyString());
    }

    @Test
    void testGetParent_NonExistentParent() throws Exception {
        when(parentService.getParent(eq("fake-token"))).thenThrow(new RuntimeException("parent not found"));

        mockMvc.perform(get("/api/parent")
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isNotFound());

        verify(parentService, times(1)).getParent(eq("fake-token"));
    }

    // PUT /api/parent
    @Test
    void testUpdateParent_Success() throws Exception {
        ParentUpdateRequestDTO request = ParentUpdateRequestDTO.builder()
                .name("Updated Name")
                .phone("0987654321")
                .gender(false)
                .profilePictureType(Parent.ProfilePictureType.AVATAR_1)
                .build();

        ParentResponseDTO response = new ParentResponseDTO();
        response.setId(1L);
        response.setName("Updated Name");
        response.setPhone("0987654321");
        response.setEmail("john.smith@email.com");
        response.setGender(false);
        response.setProfilePictureType(Parent.ProfilePictureType.AVATAR_1);

        when(parentService.updateParent(eq("fake-token"), any(ParentUpdateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"phone\":\"0987654321\",\"gender\":false,\"profilePictureType\":\"AVATAR_1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("0987654321"))
                .andExpect(jsonPath("$.gender").value(false))
                .andExpect(jsonPath("$.profilePictureType").value("AVATAR_1"));

        verify(parentService, times(1)).updateParent(eq("fake-token"), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_MissingAuthorization() throws Exception {
        mockMvc.perform(put("/api/parent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"phone\":\"0987654321\",\"gender\":false,\"profilePictureType\":\"AVATAR_1\"}"))
                .andExpect(status().isBadRequest());

        verify(parentService, never()).updateParent(anyString(), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_InvalidAuthorization() throws Exception {
        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "InvalidHeader")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"phone\":\"0987654321\",\"gender\":false,\"profilePictureType\":\"AVATAR_1\"}"))
                .andExpect(status().isBadRequest());

        verify(parentService, never()).updateParent(anyString(), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_NonExistentParent() throws Exception {
        when(parentService.updateParent(eq("fake-token"), any(ParentUpdateRequestDTO.class)))
                .thenThrow(new RuntimeException("parent not found"));

        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"phone\":\"0987654321\",\"gender\":false,\"profilePictureType\":\"AVATAR_1\"}"))
                .andExpect(status().isNotFound());

        verify(parentService, times(1)).updateParent(eq("fake-token"), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_InvalidRequest_TooLongName() throws Exception {
        String longName = new String(new char[101]).replace('\0', 'a'); // 101 characters

        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + longName + "\",\"phone\":\"0987654321\",\"gender\":false,\"profilePictureType\":\"AVATAR_1\"}"))
                .andExpect(status().isBadRequest());

        verify(parentService, never()).updateParent(anyString(), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_InvalidRequest_TooLongPhone() throws Exception {
        String longPhone = new String(new char[21]).replace('\0', '1'); // 21 characters

        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"phone\":\"" + longPhone + "\",\"gender\":false,\"profilePictureType\":\"AVATAR_1\"}"))
                .andExpect(status().isBadRequest());

        verify(parentService, never()).updateParent(anyString(), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_InvalidRequest_TooLongProfilePictureName() throws Exception {
        String longProfilePictureName = new String(new char[501]).replace('\0', 'a'); // 501 characters

        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"phone\":\"0987654321\",\"gender\":false,\"profilePictureType\":\"AVATAR_1\",\"profilePictureName\":\"" + longProfilePictureName + "\"}"))
                .andExpect(status().isBadRequest());

        verify(parentService, never()).updateParent(anyString(), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_InvalidRequest_TooLongProfilePictureContentType() throws Exception {
        String longContentType = new String(new char[101]).replace('\0', 'a'); // 101 characters

        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"phone\":\"0987654321\",\"gender\":false,\"profilePictureType\":\"AVATAR_1\",\"profilePictureContentType\":\"" + longContentType + "\"}"))
                .andExpect(status().isBadRequest());

        verify(parentService, never()).updateParent(anyString(), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_PartialUpdate_Success() throws Exception {
        ParentUpdateRequestDTO request = ParentUpdateRequestDTO.builder()
                .name("Partial Update")
                .build();

        ParentResponseDTO response = new ParentResponseDTO();
        response.setId(1L);
        response.setName("Partial Update");
        response.setPhone("1234567890");
        response.setEmail("john.smith@email.com");
        response.setGender(true);
        response.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);

        when(parentService.updateParent(eq("fake-token"), any(ParentUpdateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Partial Update\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Partial Update"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.email").value("john.smith@email.com"))
                .andExpect(jsonPath("$.gender").value(true))
                .andExpect(jsonPath("$.profilePictureType").value("DEFAULT"));

        verify(parentService, times(1)).updateParent(eq("fake-token"), any(ParentUpdateRequestDTO.class));
    }

    @Test
    void testUpdateParent_WithProfilePicture_Success() throws Exception {
        byte[] profilePictureContent = "test image content".getBytes();
        ParentUpdateRequestDTO request = ParentUpdateRequestDTO.builder()
                .name("Updated Name")
                .phone("0987654321")
                .gender(false)
                .profilePictureType(Parent.ProfilePictureType.CUSTOM)
                .profilePictureName("profile.jpg")
                .profilePictureContent(profilePictureContent)
                .profilePictureSize(18L)
                .profilePictureContentType("image/jpeg")
                .build();

        ParentResponseDTO response = new ParentResponseDTO();
        response.setId(1L);
        response.setName("Updated Name");
        response.setPhone("0987654321");
        response.setEmail("john.smith@email.com");
        response.setGender(false);
        response.setProfilePictureType(Parent.ProfilePictureType.CUSTOM);
        response.setProfilePictureName("profile.jpg");
        response.setProfilePictureContent(profilePictureContent);
        response.setProfilePictureSize(18L);
        response.setProfilePictureContentType("image/jpeg");

        when(parentService.updateParent(eq("fake-token"), any(ParentUpdateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/parent")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\",\"phone\":\"0987654321\",\"gender\":false,\"profilePictureType\":\"CUSTOM\",\"profilePictureName\":\"profile.jpg\",\"profilePictureContent\":\"" + java.util.Base64.getEncoder().encodeToString(profilePictureContent) + "\",\"profilePictureSize\":18,\"profilePictureContentType\":\"image/jpeg\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("0987654321"))
                .andExpect(jsonPath("$.gender").value(false))
                .andExpect(jsonPath("$.profilePictureType").value("CUSTOM"))
                .andExpect(jsonPath("$.profilePictureName").value("profile.jpg"))
                .andExpect(jsonPath("$.profilePictureSize").value(18))
                .andExpect(jsonPath("$.profilePictureContentType").value("image/jpeg"));

        verify(parentService, times(1)).updateParent(eq("fake-token"), any(ParentUpdateRequestDTO.class));
    }
}