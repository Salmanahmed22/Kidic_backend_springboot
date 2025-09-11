package com.example.kidic.service;

import com.example.kidic.config.JwtService;
import com.example.kidic.dto.ChildRequestDTO;
import com.example.kidic.dto.ChildResponseDTO;
import com.example.kidic.entity.Child;
import com.example.kidic.entity.Family;
import com.example.kidic.repository.ChildRepository;
import com.example.kidic.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChildService {
    @Autowired
    private ChildRepository childRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private FamilyRepository familyRepository;
    public ChildResponseDTO create(ChildRequestDTO request, String token) {
        UUID familyId = jwtService.extractFamilyId(token);

        if (familyId == null) {
            throw new IllegalArgumentException("Parent does not belong to any family");
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));

        Child child = new Child();
        child.setName(request.getName());
        child.setGender(request.getGender());
        child.setDateOfBirth(request.getDateOfBirth());
        child.setFamily(family);

        childRepository.save(child);
        ChildResponseDTO response = toResponseDTO(child);
        return response;
    }

    public static Child toEntity(ChildRequestDTO dto, Family family) {
        Child child = new Child();
        child.setName(dto.getName());
        child.setGender(dto.getGender());
        child.setDateOfBirth(dto.getDateOfBirth());
        child.setMedicalNotes(dto.getMedicalNotes());
        child.setFamily(family);
        return child;
    }

    // Entity -> ResponseDTO
    public static ChildResponseDTO toResponseDTO(Child child) {
        return ChildResponseDTO.builder()
                .name(child.getName())
                .gender(child.getGender())
                .dateOfBirth(child.getDateOfBirth())
                .medicalNotes(child.getMedicalNotes())
                .build();
    }

}
