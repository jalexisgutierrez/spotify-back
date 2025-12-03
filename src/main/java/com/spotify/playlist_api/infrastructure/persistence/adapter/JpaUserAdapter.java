package com.spotify.playlist_api.infrastructure.persistence.adapter;

import com.spotify.playlist_api.domain.model.User;
import com.spotify.playlist_api.domain.model.UserRole;
import com.spotify.playlist_api.domain.port.UserRepositoryPort;
import com.spotify.playlist_api.infrastructure.persistence.entity.UserEntity;
import com.spotify.playlist_api.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaUserAdapter implements UserRepositoryPort {

    private final JpaUserRepository userRepository;

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();

        UserEntity saved = userRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .role(UserRole.valueOf(entity.getRole()))
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
