package com.example.kidic.controller;

import com.example.kidic.dto.ChildRequestDTO;
import com.example.kidic.dto.ChildResponseDTO;
import com.example.kidic.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "api/child")
public class ChildController {
    @Autowired
    private ChildService childService;

    @PostMapping
    public ResponseEntity<ChildResponseDTO> create(
            @RequestBody ChildRequestDTO child,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(childService.create(child, token));
    }
}
