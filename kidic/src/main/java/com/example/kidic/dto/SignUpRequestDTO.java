package com.example.kidic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDTO {
    private String name;
    private String phone;
    private String email;
    private Boolean gender; // true for male, false for female
    private String password; // Hashed String

}
