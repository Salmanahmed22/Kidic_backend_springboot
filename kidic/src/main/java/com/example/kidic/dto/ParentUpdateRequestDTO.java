package com.example.kidic.dto;

import com.example.kidic.entity.Parent;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParentUpdateRequestDTO {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    private Boolean gender;

    private Parent.ProfilePictureType profilePictureType;

    @Size(max = 500, message = "Profile picture name must not exceed 500 characters")
    private String profilePictureName;

    private byte[] profilePictureContent;

    private Long profilePictureSize;

    @Size(max = 100, message = "Profile picture content type must not exceed 100 characters")
    private String profilePictureContentType;
}
