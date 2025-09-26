package com.example.kidic.controller;

import com.example.kidic.dto.ReviewRequestDTO;
import com.example.kidic.dto.ReviewResponseDTO;
import com.example.kidic.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    // Test cases for add endpoint

    @Test
    void add_shouldReturnReviewResponseDTOWhenValid() throws Exception {
        // Pass case: Valid input
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great app!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(4);

        ReviewResponseDTO responseDTO = ReviewResponseDTO.builder()
                .id(1L)
                .content("Great app!")
                .type("NORMAL")
                .stars(4)
                .createdAt(LocalDateTime.now())
                .parentId(1L)
                .parentName("John Doe")
                .build();

        when(reviewService.add(anyString(), any(ReviewRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Great app!\",\"type\":\"NORMAL\",\"stars\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Great app!"))
                .andExpect(jsonPath("$.type").value("NORMAL"))
                .andExpect(jsonPath("$.stars").value(4));
    }

    @Test
    void add_shouldFailWhenMissingAuthorizationHeader() throws Exception {
        // Fail case: Missing Authorization header
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great app!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(4);

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Great app!\",\"type\":\"NORMAL\",\"stars\":4}"))
                .andExpect(status().is(401)); // Unauthorized
    }

    @Test
    void add_shouldFailWhenContentIsBlank() throws Exception {
        // Fail case: Blank content
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(4);

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"\",\"type\":\"NORMAL\",\"stars\":4}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_shouldFailWhenContentExceedsLength() throws Exception {
        // Fail case: Content exceeds 1000 characters
        String longContent = "x".repeat(1001);
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent(longContent);
        requestDTO.setType("NORMAL");
        requestDTO.setStars(4);

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"" + longContent + "\",\"type\":\"NORMAL\",\"stars\":4}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_shouldFailWhenTypeIsNull() throws Exception {
        // Fail case: Null type
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great app!");
        requestDTO.setType(null);
        requestDTO.setStars(4);

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Great app!\",\"type\":null,\"stars\":4}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_shouldFailWhenStarsBelowMin() throws Exception {
        // Fail case: Stars below 1
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great app!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(0);

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Great app!\",\"type\":\"NORMAL\",\"stars\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_shouldFailWhenStarsAboveMax() throws Exception {
        // Fail case: Stars above 5
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great app!");
        requestDTO.setType("NORMAL");
        requestDTO.setStars(6);

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Great app!\",\"type\":\"NORMAL\",\"stars\":6}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void add_shouldFailWhenInvalidType() throws Exception {
        // Fail case: Invalid type (not NORMAL or COMPLAIN)
        ReviewRequestDTO requestDTO = new ReviewRequestDTO();
        requestDTO.setContent("Great app!");
        requestDTO.setType("INVALID");
        requestDTO.setStars(4);

        mockMvc.perform(post("/api/review")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Great app!\",\"type\":\"INVALID\",\"stars\":4}"))
                .andExpect(status().isBadRequest());
    }

    // Test cases for getNormalReviews endpoint

    @Test
    void getNormalReviews_shouldReturnListOfReviewResponseDTOWhenValid() throws Exception {
        // Pass case: Valid request
        ReviewResponseDTO responseDTO = ReviewResponseDTO.builder()
                .id(1L)
                .content("Great app!")
                .type("NORMAL")
                .stars(4)
                .createdAt(LocalDateTime.now())
                .parentId(1L)
                .parentName("John Doe")
                .build();

        List<ReviewResponseDTO> reviews = Collections.singletonList(responseDTO);
        when(reviewService.getNormalReviews()).thenReturn(reviews);

        mockMvc.perform(get("/api/review/normal-reviews")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Great app!"))
                .andExpect(jsonPath("$[0].type").value("NORMAL"))
                .andExpect(jsonPath("$[0].stars").value(4));
    }

    @Test
    void getNormalReviews_shouldReturnEmptyListWhenNoReviews() throws Exception {
        // Pass case: Empty list
        when(reviewService.getNormalReviews()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/review/normal-reviews")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getNormalReviews_shouldFailWhenMissingAuthorizationHeader() throws Exception {
        // Fail case: Missing Authorization header
        mockMvc.perform(get("/api/review/normal-reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401)); // Unauthorized
    }

    // Test cases for getComplains endpoint

    @Test
    void getComplains_shouldReturnListOfReviewResponseDTOWhenValid() throws Exception {
        // Pass case: Valid request
        ReviewResponseDTO responseDTO = ReviewResponseDTO.builder()
                .id(2L)
                .content("Bad experience")
                .type("COMPLAIN")
                .stars(2)
                .createdAt(LocalDateTime.now())
                .parentId(2L)
                .parentName("Jane Smith")
                .build();

        List<ReviewResponseDTO> complains = Collections.singletonList(responseDTO);
        when(reviewService.getComplains()).thenReturn(complains);

        mockMvc.perform(get("/api/review/complains")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].content").value("Bad experience"))
                .andExpect(jsonPath("$[0].type").value("COMPLAIN"))
                .andExpect(jsonPath("$[0].stars").value(2));
    }

    @Test
    void getComplains_shouldReturnEmptyListWhenNoComplains() throws Exception {
        // Pass case: Empty list
        when(reviewService.getComplains()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/review/complains")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getComplains_shouldFailWhenMissingAuthorizationHeader() throws Exception {
        // Fail case: Missing Authorization header
        mockMvc.perform(get("/api/review/complains")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401)); // Unauthorized
    }
}