package com.example.kidic.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ParentDetailsDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Boolean gender;

    private UUID familyId;

    private List<ChildDetailsDTO> children;
}