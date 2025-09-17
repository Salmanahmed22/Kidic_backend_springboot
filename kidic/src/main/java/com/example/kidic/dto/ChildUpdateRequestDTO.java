package com.example.kidic.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChildUpdateRequestDTO {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 1000, message = "Medical notes must not exceed 1000 characters")
    private String medicalNotes;
}
