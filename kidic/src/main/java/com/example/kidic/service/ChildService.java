package com.example.kidic.service;

import com.example.kidic.config.JwtService;
import com.example.kidic.dto.ChildRequestDTO;
import com.example.kidic.dto.ChildResponseDTO;
import com.example.kidic.dto.ChildUpdateRequestDTO;
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
    @Autowired
    private FamilyService familyService;
    public ChildResponseDTO create(ChildRequestDTO request, String token) {
        UUID familyId = jwtService.extractFamilyId(token);

        if (familyId == null) {
            throw new IllegalArgumentException("Parent does not belong to any family");
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found"));

        Child child = toEntity(request,family);

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
                .id(child.getId())
                .name(child.getName())
                .gender(child.getGender())
                .dateOfBirth(child.getDateOfBirth())
                .medicalNotes(child.getMedicalNotes())
                .build();
    }

    public String delete(Long childId, String token) {
        UUID familyId = jwtService.extractFamilyId(token);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        if (!familyService.isChildMember(familyId, child)) {
            throw new IllegalArgumentException("Child does not belong to any family");
        }

        familyService.deleteChild(familyId,child);
        childRepository.delete(child);
        return "Child deleted";
    }

    public ChildResponseDTO update(Long childId, ChildUpdateRequestDTO requestDTO, String token) {
        System.out.println(requestDTO);
        UUID familyId = jwtService.extractFamilyId(token);
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        if (!familyService.isChildMember(familyId, child)) {
            throw new IllegalArgumentException("Child does not belong to any family");
        }
        if (requestDTO.getName() != null) {
            child.setName(requestDTO.getName());
        }

        if (requestDTO.getDateOfBirth() != null) {
            child.setDateOfBirth(requestDTO.getDateOfBirth());
        }
        if (requestDTO.getMedicalNotes() != null) {
            child.setMedicalNotes(requestDTO.getMedicalNotes());
        }
        childRepository.save(child);
        ChildResponseDTO response = toResponseDTO(child);
        return response;
    }
}
