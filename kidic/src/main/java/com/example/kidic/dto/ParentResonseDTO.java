package com.example.kidic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParentResonseDTO {
    private Long id;

    private String name;

    private String phone;

    private String email;

    private Boolean gender;
}
