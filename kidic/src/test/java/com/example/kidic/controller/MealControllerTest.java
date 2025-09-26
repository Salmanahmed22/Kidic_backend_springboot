package com.example.kidic.controller;

import com.example.kidic.entity.Meal;
import com.example.kidic.service.MealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MealControllerTest {

    @Mock
    private MealService mealService;

    @InjectMocks
    private MealController mealController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mealController).build();
    }

    // Test cases for listForChild endpoint

    @Test
    void listForChild_shouldReturnListOfMealsWhenValid() throws Exception {
        // Pass case: Valid childId
        List<Meal> meals = Collections.singletonList(new Meal("Pancakes", List.of("Flour", "Eggs"), "Mix and cook"));
        when(mealService.listForChild(1L)).thenReturn(meals);

        mockMvc.perform(get("/api/meals/children/1")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Pancakes"))
                .andExpect(jsonPath("$[0].ingredients[0]").value("Flour"))
                .andExpect(jsonPath("$[0].recipe").value("Mix and cook"));
    }

    @Test
    void listForChild_shouldFailWhenMissingAuthorizationHeader() throws Exception {
        // Fail case: Missing Authorization header
        mockMvc.perform(get("/api/meals/children/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401)); // Unauthorized
    }

    // Test cases for addForChild endpoint

    @Test
    void addForChild_shouldReturnCreatedMealWhenValid() throws Exception {
        // Pass case: Valid input
        Meal meal = new Meal("Pancakes", List.of("Flour", "Eggs"), "Mix and cook");
        when(mealService.addForChild(1L, "Pancakes", List.of("Flour", "Eggs"), "Mix and cook")).thenReturn(meal);

        mockMvc.perform(post("/api/meals/children/1")
                        .header("Authorization", "Bearer test-token")
                        .param("title", "Pancakes")
                        .param("ingredients", "Flour,Eggs")
                        .param("recipe", "Mix and cook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Pancakes"))
                .andExpect(jsonPath("$.ingredients[0]").value("Flour"))
                .andExpect(jsonPath("$.recipe").value("Mix and cook"));
    }

    @Test
    void addForChild_shouldFailWhenMissingAuthorizationHeader() throws Exception {
        // Fail case: Missing Authorization header
        mockMvc.perform(post("/api/meals/children/1")
                        .param("title", "Pancakes")
                        .param("ingredients", "Flour,Eggs")
                        .param("recipe", "Mix and cook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401)); // Unauthorized
    }

    @Test
    void addForChild_shouldFailWhenMissingTitle() throws Exception {
        // Fail case: Missing title
        mockMvc.perform(post("/api/meals/children/1")
                        .header("Authorization", "Bearer test-token")
                        .param("ingredients", "Flour,Eggs")
                        .param("recipe", "Mix and cook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Test cases for editForChild endpoint

    @Test
    void editForChild_shouldReturnUpdatedMealWhenValid() throws Exception {
        // Pass case: Valid input
        Meal meal = new Meal("Updated Pancakes", List.of("Flour", "Eggs", "Milk"), "Mix, cook, serve");
        when(mealService.editForChild(1L, 1L, "Updated Pancakes", List.of("Flour", "Eggs", "Milk"), "Mix, cook, serve")).thenReturn(meal);

        mockMvc.perform(put("/api/meals/children/1/1")
                        .header("Authorization", "Bearer test-token")
                        .param("title", "Updated Pancakes")
                        .param("ingredients", "Flour,Eggs,Milk")
                        .param("recipe", "Mix, cook, serve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Pancakes"))
                .andExpect(jsonPath("$.ingredients[2]").value("Milk"))
                .andExpect(jsonPath("$.recipe").value("Mix, cook, serve"));
    }

    @Test
    void editForChild_shouldFailWhenMissingAuthorizationHeader() throws Exception {
        // Fail case: Missing Authorization header
        mockMvc.perform(put("/api/meals/children/1/1")
                        .param("title", "Updated Pancakes")
                        .param("ingredients", "Flour,Eggs,Milk")
                        .param("recipe", "Mix, cook, serve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401)); // Unauthorized
    }

    @Test
    void editForChild_shouldFailWhenMealNotFound() throws Exception {
        // Fail case: Meal not found
        doThrow(new IllegalArgumentException("Meal not found")).when(mealService).editForChild(anyLong(), anyLong(), anyString(), any(), anyString());

        mockMvc.perform(put("/api/meals/children/1/999")
                        .header("Authorization", "Bearer test-token")
                        .param("title", "Updated Pancakes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // Test cases for deleteForChild endpoint

    @Test
    void deleteForChild_shouldReturnNoContentWhenValid() throws Exception {
        // Pass case: Valid deletion
        doNothing().when(mealService).deleteForChild(1L, 1L);

        mockMvc.perform(delete("/api/meals/children/1/1")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteForChild_shouldFailWhenMissingAuthorizationHeader() throws Exception {
        // Fail case: Missing Authorization header
        mockMvc.perform(delete("/api/meals/children/1/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401)); // Unauthorized
    }

    @Test
    void deleteForChild_shouldFailWhenMealNotFound() throws Exception {
        // Fail case: Meal not found
        doThrow(new IllegalArgumentException("Meal not found")).when(mealService).deleteForChild(anyLong(), anyLong());

        mockMvc.perform(delete("/api/meals/children/1/999")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}