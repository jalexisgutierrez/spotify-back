package com.spotify.playlist_api.domain.port;

import com.spotify.playlist_api.domain.model.User;

public interface TokenServicePort {
    record Token(String value, long issueAt, long expiresAt) {}
    Token create(User  user);
    String roleFromToken(String token);
}
