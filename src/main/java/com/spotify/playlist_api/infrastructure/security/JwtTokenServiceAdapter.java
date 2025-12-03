package com.spotify.playlist_api.infrastructure.security;

import com.spotify.playlist_api.domain.model.User;
import com.spotify.playlist_api.domain.port.TimeProviderPort;
import com.spotify.playlist_api.domain.port.TokenServicePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenServiceAdapter implements TokenServicePort {

    private final SecretKey key;
    private final long expirationMs;
    private final TimeProviderPort time;

    public JwtTokenServiceAdapter(@Value("${jwt.secret}") String secret,
                                  @Value("${jwt.expiration}") long expirationMs,
                                  TimeProviderPort time) {
        // Decodificar la clave Base64 (igual que en JwtAuthFilter)
        byte[] keyBytes;
        try {
            // Intenta decodificar como Base64 primero
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            // Si falla, usa el string directamente (para desarrollo)
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
        this.time = time;
    }

    @Override
    public Token create(User user) {
        long now = time.nowMillis();
        long exp = now + expirationMs;

        String token = Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("uid", user.getId() == null ? null : user.getId().toString())
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        return new Token(token, now, exp);
    }

    @Override
    public String roleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
