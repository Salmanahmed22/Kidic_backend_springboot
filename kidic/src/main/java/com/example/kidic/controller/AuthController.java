package com.example.kidic.controller;

import com.example.kidic.dto.AuthResponseDTO;
import com.example.kidic.dto.LoginRequestDTO;
import com.example.kidic.dto.SignUpRequestDTO;
import com.example.kidic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping("signup")
    public ResponseEntity<AuthResponseDTO> signUp(@RequestBody SignUpRequestDTO request){
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));

    }
}
