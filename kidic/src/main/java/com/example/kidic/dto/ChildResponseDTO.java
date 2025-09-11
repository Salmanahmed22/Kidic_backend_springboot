package com.example.kidic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ChildResponseDTO {
    private String name;
    private Boolean gender;
    private LocalDate dateOfBirth;
    private String medicalNotes;
}

