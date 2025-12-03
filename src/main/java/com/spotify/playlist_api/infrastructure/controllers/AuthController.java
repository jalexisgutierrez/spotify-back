package com.spotify.playlist_api.infrastructure.controllers;

import com.spotify.playlist_api.application.AuthUseCase;
import com.spotify.playlist_api.infrastructure.controllers.dtos.LoginRequest;
import com.spotify.playlist_api.infrastructure.controllers.dtos.LoginResponse;
import com.spotify.playlist_api.infrastructure.controllers.dtos.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var token = authUseCase.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token.value(), token.issueAt(), token.expiresAt()));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        var user = authUseCase.register(request.getEmail(), request.getPassword(), request.getRole());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Usuario registrado exitosamente",
                        "userId", user.getId().toString(),
                        "email", user.getEmail()
                ));
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        var user = authUseCase.registerAdmin(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Map.of(
                "message", "Administrador registrado",
                "userId", user.getId().toString()
        ));
    }

    @PostMapping("/register-socio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> registerSocio(@Valid @RequestBody RegisterRequest request) {
        var user = authUseCase.registerUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Map.of(
                "message", "Usuario registrado",
                "userId", user.getId().toString()
        ));
    }
}
