package com.facilitydesk.facility_desk.controller;

import com.facilitydesk.facility_desk.dto.AuthDto;
import com.facilitydesk.facility_desk.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthDto.JwtResponse> authenticateUser(
            @Valid @RequestBody AuthDto.LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user account")
    public ResponseEntity<AuthDto.MessageResponse> registerUser(
            @Valid @RequestBody AuthDto.RegisterRequest signUpRequest) {
        log.info("Registration attempt for user: {}", signUpRequest.getUsername());
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }
}
