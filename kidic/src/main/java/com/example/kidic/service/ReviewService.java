package com.example.kidic.service;

import com.example.kidic.config.JwtService;
import com.example.kidic.dto.ReviewRequestDTO;
import com.example.kidic.dto.ReviewResponseDTO;
import com.example.kidic.entity.Parent;
import com.example.kidic.entity.Review;
import com.example.kidic.repository.ParentRepository;
import com.example.kidic.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ParentRepository parentRepository;

    public ReviewResponseDTO add(String token, ReviewRequestDTO requestDTO) {
        String email = jwtService.extractUsername(token);
        Parent parent = parentRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("parent not found"));
        Review review = toEntity(requestDTO, parent);
        reviewRepository.save(review);
        return toDto(review);
    }

    public static Review toEntity(ReviewRequestDTO dto, Parent parent) {
        return Review.builder()
                .content(dto.getContent())
                .type(Review.ReviewType.valueOf(dto.getType()))
                .stars(dto.getStars())
                .parent(parent)
                .build();
    }

    public static ReviewResponseDTO toDto(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .content(review.getContent())
                .type(review.getType().name())
                .stars(review.getStars())
                .createdAt(review.getCreatedAt())
                .parentId(review.getParent().getId())
                .parentName(review.getParent().getName())
                .build();
    }

    public List<ReviewResponseDTO> getNormalReviews() {
        List<Review> reviews = reviewRepository.findByType(Review.ReviewType.NORMAL);
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(toDto(review));
        }
        return dtos;
    }

    public List<ReviewResponseDTO> getComplains() {
        List<Review> reviews = reviewRepository.findByType(Review.ReviewType.COMPLAIN);
        List<ReviewResponseDTO> dtos = new ArrayList<>();
        for (Review review : reviews) {
            dtos.add(toDto(review));
        }
        return dtos;
    }
}
