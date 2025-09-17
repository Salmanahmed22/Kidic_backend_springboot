package com.example.kidic.controller;

import com.example.kidic.dto.ChildRequestDTO;
import com.example.kidic.dto.ChildResponseDTO;
import com.example.kidic.dto.ChildUpdateRequestDTO;
import com.example.kidic.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/child")
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

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long childId
            , @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(childService.delete(childId, token));
    }

    @PutMapping("{id}")
    public ResponseEntity<ChildResponseDTO> update(@PathVariable("id") Long childId
            ,@RequestBody ChildUpdateRequestDTO requestDTO,
                @RequestHeader("Authorization") String authHeader)

    {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(childService.update(childId, requestDTO,token));
    }

}
