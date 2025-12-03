package com.spotify.playlist_api.infrastructure.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private long issuedAt;
    private long expiresAt;
}
