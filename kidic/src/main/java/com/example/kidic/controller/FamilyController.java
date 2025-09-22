package com.example.kidic.controller;

import com.example.kidic.dto.ChildResponseDTO;
import com.example.kidic.dto.FamilyResponseDTO;
import com.example.kidic.dto.ParentResponseDTO;
import com.example.kidic.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/family")
public class FamilyController {
    @Autowired
    private FamilyService familyService;
    @GetMapping
    public ResponseEntity<FamilyResponseDTO> getFamily
            (@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(familyService.getFamily(token));
    }
    @GetMapping("parents")
    public ResponseEntity<List<ParentResponseDTO>> getFamilyParents
            (@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(familyService.getFamilyParents(token));
    }
    @GetMapping("children")
    public ResponseEntity<List<ChildResponseDTO>> getFamilyChildren
            (@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(familyService.getFamilyChildren(token));
    }
}
