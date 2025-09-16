package com.example.kidic.controller;

import com.example.kidic.dto.AuthResponseDTO;
import com.example.kidic.dto.LoginRequestDTO;
import com.example.kidic.dto.SignUpExistingFamilyRequestDTO;
import com.example.kidic.dto.SignUpNewFamilyRequestDTO;
import com.example.kidic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping("signup/new-family")
    public ResponseEntity<AuthResponseDTO> signUpNewFamily(@RequestBody SignUpNewFamilyRequestDTO request){
        System.out.println("new-familyyyyy");
        return ResponseEntity.ok(authService.signUpNewFamily(request));
    }

    @PostMapping("signup/existing-family")
    public ResponseEntity<AuthResponseDTO> signUpExistingFamily(@RequestBody SignUpExistingFamilyRequestDTO request){
        return ResponseEntity.ok(authService.signUpExistingFamily(request));
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));

    }
}
