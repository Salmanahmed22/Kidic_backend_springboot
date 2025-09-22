package com.example.kidic.controller;

import com.example.kidic.dto.ReviewRequestDTO;
import com.example.kidic.dto.ReviewResponseDTO;
import com.example.kidic.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> add(
            @RequestHeader("Authorization") String authHead,
            @RequestBody ReviewRequestDTO requestDTO){
        String token = authHead.substring(7);
        return ResponseEntity.ok(reviewService.add(token,requestDTO));
    }
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> get(
            @RequestHeader("Authorization") String authHead){
        return ResponseEntity.ok(reviewService.get());
    }
}
