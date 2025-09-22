package com.example.kidic.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDTO {

    private Long id;
    private String content;
    private String type;
    private Integer stars;
    private LocalDateTime createdAt;
    private Long parentId;
    private String parentName;
}
