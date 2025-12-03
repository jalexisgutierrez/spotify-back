package com.spotify.playlist_api.domain.port;

public interface PasswordHasherPort {
    String hash(String password);
    boolean verify(String password, String hashedPassword);
}
