package com.example.kidic.controller;

import com.example.kidic.dto.ParentResponseDTO;
import com.example.kidic.dto.ParentUpdateRequestDTO;
import com.example.kidic.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/parent")
public class ParentController {
    @Autowired
    private ParentService parentService;
    @GetMapping
    public ResponseEntity<ParentResponseDTO> getParent(
            @RequestHeader("Authorization") String authHead) {
        String token = authHead.substring(7);
        return ResponseEntity.ok(parentService.getParent(token));
    }

    @PutMapping
    public ResponseEntity<ParentResponseDTO> updateParent(
            @RequestHeader("Authorization") String authHead,
            @RequestBody ParentUpdateRequestDTO requestDto
    ){
        String token = authHead.substring(7);
        return ResponseEntity.ok(parentService.updateParent(token, requestDto));
    }
}
