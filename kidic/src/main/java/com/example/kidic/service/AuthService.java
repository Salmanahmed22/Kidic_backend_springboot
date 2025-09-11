package com.example.kidic.service;

import com.example.kidic.config.JwtService;
import com.example.kidic.dto.AuthResponseDTO;
import com.example.kidic.dto.LoginRequestDTO;
import com.example.kidic.dto.SignUpRequestDTO;
import com.example.kidic.entity.Parent;
import com.example.kidic.repository.ParentRepository;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @SneakyThrows
    public AuthResponseDTO signUp(SignUpRequestDTO request) {
        if (parentRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        Parent parent = new Parent();
        parent.setName(request.getName());
        parent.setPhone(request.getPhone());
        parent.setEmail(request.getEmail());
        parent.setGender(request.getGender());
        parent.setPassword(passwordEncoder.encode(request.getPassword()));
        parentRepository.save(parent);
        String token = jwtService.generateToken(parent);
        AuthResponseDTO response = AuthResponseDTO.builder().token(token).build();
        return response;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Parent parent = parentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials!"));
        String token = jwtService.generateToken(parent);
        AuthResponseDTO response = AuthResponseDTO.builder().token(token).build();
        return response;
    }
}
