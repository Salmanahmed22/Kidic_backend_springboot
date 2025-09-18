package com.example.kidic.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FamilyResponseDTO {
    private List<ChildResponseDTO> children;
    private List<ParentResonseDTO> parents;
}
