package com.example.kidic.dto;

import com.example.kidic.entity.Parent;
import lombok.Builder;
import lombok.Data;

@Data
public class ParentResponseDTO {
    private Long id;

    private String name;

    private String phone;

    private String email;

    private Boolean gender;

    private Parent.ProfilePictureType profilePictureType;

    private String profilePictureName;

    private byte[] profilePictureContent;

    private Long profilePictureSize;

    private String profilePictureContentType;
}
