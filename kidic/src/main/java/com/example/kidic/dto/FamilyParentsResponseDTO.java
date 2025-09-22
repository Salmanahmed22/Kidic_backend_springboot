package com.example.kidic.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class FamilyParentsResponseDTO {
    private List<ParentResponseDTO> parents;
}
