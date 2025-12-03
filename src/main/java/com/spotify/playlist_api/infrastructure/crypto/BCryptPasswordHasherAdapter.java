package com.spotify.playlist_api.infrastructure.crypto;

import com.spotify.playlist_api.domain.port.PasswordHasherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String password) {

        String hash = encoder.encode(password);
        log.info("🔐 [BCrypt] Hash generado para '{}': {}", password, hash);
        log.info("🔐 [BCrypt] Longitud del hash: {}", hash.length());
        return hash;
    }

    @Override
    public boolean verify(String password, String hashedPassword) {

        log.info("🔍 [BCrypt] Verificando contraseña...");
        log.info("🔍 [BCrypt] Contraseña ingresada: '{}'", password);
        log.info("🔍 [BCrypt] Hash en BD: '{}'", hashedPassword);
        log.info("🔍 [BCrypt] Longitud del hash: {}", hashedPassword.length());

        boolean matches = encoder.matches(password, hashedPassword);

        log.info("🔍 [BCrypt] Resultado: {}", matches ? "✅ COINCIDEN" : "❌ NO COINCIDEN");

        // Debug adicional
        if (!matches) {
            String testHash = encoder.encode(password);
            log.warn("⚠️ [BCrypt] Hash de prueba generado: {}", testHash);
            log.warn("⚠️ [BCrypt] ¿'{}' coincide con hash de prueba? {}", password, encoder.matches(password, testHash));
        }

        return matches;
    }
}
