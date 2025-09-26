package com.example.kidic.controller;

import com.example.kidic.dto.ReviewRequestDTO;
import com.example.kidic.dto.ReviewResponseDTO;
import com.example.kidic.dto.SignUpNewFamilyRequestDTO;
import com.example.kidic.repository.ParentRepository;
import com.example.kidic.repository.ReviewRepository;
import com.example.kidic.service.AuthService;
import com.example.kidic.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private String token;
    private Long parentId;

    @BeforeEach
    void setUp() {
        // Clear repositories
        reviewRepository.deleteAll();
        parentRepository.deleteAll();

        // Create a parent and family using AuthService
        SignUpNewFamilyRequestDTO signUpRequest = SignUpNewFamilyRequestDTO.builder()
                .name("Test Parent")
                .phone("1234567890")
                .email("test@example.com")
                .gender(true)
                .password("password123")
                .build();

        token = "Bearer " + authService.signUpNewFamily(signUpRequest).getToken();
        parentId = parentRepository.findByEmail("test@example.com").orElseThrow().getId();
    }

    @Test
    void addReview_ReturnsReviewResponseDTO() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great service!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(5);

        String requestJson = "{\"content\":\"Great service!\",\"type\":\"NORMAL\",\"stars\":5}";

        mockMvc.perform(post("/api/review")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":\"Great service!\",\"type\":\"NORMAL\",\"stars\":5,\"parentId\":" + parentId + ",\"parentName\":\"Test Parent\"}"));
    }

    @Test
    void getNormalReviews_ReturnsNormalReviewList() throws Exception {
        // Add a normal review
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great service!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(5);
        reviewService.add(token, requestDTO);

        mockMvc.perform(get("/api/review/normal-reviews")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"content\":\"Great service!\",\"type\":\"NORMAL\",\"stars\":5,\"parentId\":" + parentId + ",\"parentName\":\"Test Parent\"}]"));
    }

    @Test
    void getComplains_ReturnsComplainList() throws Exception {
        // Add a complain review
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Not satisfied!");
        requestDTO.setType("COMPLAIN");
        requestDTO.setStars(1);
        reviewService.add(token, requestDTO);

        mockMvc.perform(get("/api/review/complains")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"content\":\"Not satisfied!\",\"type\":\"COMPLAIN\",\"stars\":1,\"parentId\":" + parentId + ",\"parentName\":\"Test Parent\"}]"));
    }

    @Test
    void addReview_UnauthorizedWhenNoToken() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great service!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(5);

        String requestJson = "{\"content\":\"Great service!\",\"type\":\"NORMAL\",\"stars\":5}";

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addReview_UnauthorizedWhenInvalidToken() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great service!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(5);

        String requestJson = "{\"content\":\"Great service!\",\"type\":\"NORMAL\",\"stars\":5}";

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addReview_BadRequestWhenInvalidStars() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great service!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(6); // Invalid stars value

        String requestJson = "{\"content\":\"Great service!\",\"type\":\"NORMAL\",\"stars\":6}";

        mockMvc.perform(post("/api/review")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addReview_BadRequestWhenContentTooLong() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("A".repeat(1001)); // Exceeds 1000 characters
        requestDTO.setType("NORMAL");
        requestDTO.setStars(5);

        String requestJson = "{\"content\":\"" + "A".repeat(1001) + "\",\"type\":\"NORMAL\",\"stars\":5}";

        mockMvc.perform(post("/api/review")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addReview_BadRequestWhenTypeInvalid() throws Exception {
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great service!");
        requestDTO.setType("INVALID"); // Invalid ReviewType
        requestDTO.setStars(5);

        String requestJson = "{\"content\":\"Great service!\",\"type\":\"INVALID\",\"stars\":5}";

        mockMvc.perform(post("/api/review")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}