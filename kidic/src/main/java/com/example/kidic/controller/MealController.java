package com.example.kidic.controller;

import com.example.kidic.entity.Meal;
import com.example.kidic.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin(origins = "*")
public class MealController {

    @Autowired
    private MealService mealService;

    @GetMapping("/children/{childId}")
    public ResponseEntity<List<Meal>> listForChild(
            @PathVariable Long childId,
            @RequestHeader("Authorization") String authHeader) {
        extractTokenFromHeader(authHeader);
        return ResponseEntity.ok(mealService.listForChild(childId));
    }

    @PostMapping("/children/{childId}")
    public ResponseEntity<Meal> addForChild(
            @PathVariable Long childId,
            @RequestParam("title") String title,
            @RequestParam(value = "ingredients", required = false) String ingredientsCsv,
            @RequestParam(value = "recipe", required = false) String recipe,
            @RequestHeader("Authorization") String authHeader) {
        extractTokenFromHeader(authHeader);
        List<String> ingredients = ingredientsCsv == null || ingredientsCsv.isBlank()
                ? null
                : Arrays.stream(ingredientsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        Meal created = mealService.addForChild(childId, title, ingredients, recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/children/{childId}/{mealId}")
    public ResponseEntity<Meal> editForChild(
            @PathVariable Long childId,
            @PathVariable Long mealId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "ingredients", required = false) String ingredientsCsv,
            @RequestParam(value = "recipe", required = false) String recipe,
            @RequestHeader("Authorization") String authHeader) {
        extractTokenFromHeader(authHeader);
        List<String> ingredients = ingredientsCsv == null || ingredientsCsv.isBlank()
                ? null
                : Arrays.stream(ingredientsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        Meal updated = mealService.editForChild(childId, mealId, title, ingredients, recipe);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/children/{childId}/{mealId}")
    public ResponseEntity<Void> deleteForChild(
            @PathVariable Long childId,
            @PathVariable Long mealId,
            @RequestHeader("Authorization") String authHeader) {
        extractTokenFromHeader(authHeader);
        mealService.deleteForChild(childId, mealId);
        return ResponseEntity.noContent().build();
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization token is required");
    }
}


