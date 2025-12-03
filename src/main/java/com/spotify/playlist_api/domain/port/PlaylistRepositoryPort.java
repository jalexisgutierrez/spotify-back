package com.spotify.playlist_api.domain.port;

import com.spotify.playlist_api.domain.model.Playlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepositoryPort {
    Playlist save(Playlist playlist);
    Optional<Playlist> findById(UUID id);
    Optional<Playlist> findByNombre(String nombre);
    List<Playlist> findAll();
    List<Playlist> findByCreadoPor(String email);
    void delete(UUID id);
    boolean existsByNombre(String nombre);
}
