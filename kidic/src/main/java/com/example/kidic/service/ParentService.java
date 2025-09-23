package com.example.kidic.service;

import com.example.kidic.config.JwtService;
import com.example.kidic.dto.ChildDetailsDTO;
import com.example.kidic.dto.ParentDetailsDTO;
import com.example.kidic.dto.ParentResponseDTO;
import com.example.kidic.dto.ParentUpdateRequestDTO;
import com.example.kidic.entity.Child;
import com.example.kidic.entity.Parent;
import com.example.kidic.repository.ChildRepository;
import com.example.kidic.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParentService {
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ChildRepository childRepository;

    public ParentResponseDTO getParent(String token) {
        String email = jwtService.extractUsername(token);
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("parent not found"));
        return mapToResponseDTO(parent);
    }

    public ParentResponseDTO mapToResponseDTO(Parent parent) {
        ParentResponseDTO dto = new ParentResponseDTO();
        dto.setId(parent.getId());
        dto.setName(parent.getName());
        dto.setEmail(parent.getEmail());
        dto.setPhone(parent.getPhone());
        dto.setGender(parent.getGender());
        dto.setProfilePictureType(parent.getProfilePictureType());
        dto.setProfilePictureName(parent.getProfilePictureName());
        dto.setProfilePictureSize(parent.getProfilePictureSize());
        dto.setProfilePictureContentType(parent.getProfilePictureContentType());
        return dto;
    }

    public ParentResponseDTO updateParent(
            String token,
            ParentUpdateRequestDTO dto) {
        String email = jwtService.extractUsername(token);
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("parent not found"));

        if (dto.getName() != null) parent.setName(dto.getName());
        if (dto.getPhone() != null) parent.setPhone(dto.getPhone());
        if (dto.getGender() != null) parent.setGender(dto.getGender());
        if (dto.getProfilePictureType() != null) parent.setProfilePictureType(dto.getProfilePictureType());
        if (dto.getProfilePictureName() != null) parent.setProfilePictureName(dto.getProfilePictureName());
        if (dto.getProfilePictureContent() != null) parent.setProfilePictureContent(dto.getProfilePictureContent());
        if (dto.getProfilePictureSize() != null) parent.setProfilePictureSize(dto.getProfilePictureSize());
        if (dto.getProfilePictureContentType() != null) parent.setProfilePictureContentType(dto.getProfilePictureContentType());
        Parent savedParent = parentRepository.save(parent);
        return mapToResponseDTO(savedParent);
    }

    public ParentDetailsDTO getAllDetails(String token) {
        String email = jwtService.extractUsername(token);
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("parent not found"));
        List<Child> children = childRepository.findByFamily(parent.getFamily());
        return toParentDetailsDTO(parent, children);
    }


    public static ParentDetailsDTO toParentDetailsDTO(Parent parent, List<Child> children) {

        List<ChildDetailsDTO> childrenDetailsDTO = new ArrayList<>();
        for (Child child : children) {
            ChildDetailsDTO childDetailsDTO = toChildDetailsDTO(child);
            childrenDetailsDTO.add(childDetailsDTO);
        }
        return ParentDetailsDTO.builder()
                .id(parent.getId())
                .name(parent.getName())
                .phone(parent.getPhone())
                .email(parent.getEmail())
                .gender(parent.getGender())
                .familyId(parent.getFamily().getId())
                .children(childrenDetailsDTO)
                .build();
    }

    public static ChildDetailsDTO toChildDetailsDTO(Child child) {
        return ChildDetailsDTO.builder()
                .id(child.getId())
                .name(child.getName())
                .gender(child.getGender())
                .dateOfBirth(child.getDateOfBirth())
                .medicalNotes(child.getMedicalNotes())
                .medicalRecords(child.getMedicalRecords())
                .growthRecords(child.getGrowthRecords())
                .diseasesAndAllergies(child.getDiseasesAndAllergies())
                .meals(child.getMeals())
                .build();
    }
}
