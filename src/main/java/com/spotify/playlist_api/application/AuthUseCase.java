package com.spotify.playlist_api.application;

import com.spotify.playlist_api.domain.model.User;
import com.spotify.playlist_api.domain.model.UserRole;
import com.spotify.playlist_api.domain.port.PasswordHasherPort;
import com.spotify.playlist_api.domain.port.TimeProviderPort;
import com.spotify.playlist_api.domain.port.TokenServicePort;
import com.spotify.playlist_api.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenServicePort tokenService;
    private final TimeProviderPort timeProvider;

    public TokenServicePort.Token login(String email, String password) {

        log.info("🔑 [AuthUseCase] Intentando login para: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("❌ [AuthUseCase] Usuario no encontrado: {}", email);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
                });

        log.info("✅ [AuthUseCase] Usuario encontrado: {}", email);

        if (!passwordHasher.verify(password, user.getPasswordHash())) {
            log.warn("❌ [AuthUseCase] Contraseña incorrecta para: {}", email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        log.info("✅ [AuthUseCase] Login exitoso para: {}", email);
        return tokenService.create(user);
    }

    public User register(String email, String password, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .passwordHash(passwordHasher.hash(password))
                .role(role)
                .createdAt(timeProvider.now())
                .build();

        return userRepository.save(user);
    }

    public User registerAdmin(String email, String password) {
        return register(email, password, UserRole.ADMIN);
    }

    public User registerUser(String email, String password) {
        return register(email, password, UserRole.USER);
    }
}
