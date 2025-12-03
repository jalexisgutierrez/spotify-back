package com.spotify.playlist_api.infrastructure.persistence.repository;

import com.spotify.playlist_api.infrastructure.persistence.entity.PlaylistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaPlaylistRepository extends JpaRepository<PlaylistEntity, UUID> {
    Optional<PlaylistEntity> findByNombre(String nombre);
    List<PlaylistEntity> findByCreadoPor(String email);
    boolean existsByNombre(String nombre);
    void deleteByNombre(String nombre);
}
