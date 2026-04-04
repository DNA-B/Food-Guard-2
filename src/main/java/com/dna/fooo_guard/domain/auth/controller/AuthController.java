package com.dna.fooo_guard.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.auth.dto.AuthResponse;
import com.dna.fooo_guard.domain.auth.dto.LoginRequest;
import com.dna.fooo_guard.domain.auth.dto.SignUpRequest;
import com.dna.fooo_guard.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest dto) {
        return ResponseEntity.ok(authService.signUp(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
