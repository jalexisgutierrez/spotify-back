package com.spotify.playlist_api.infrastructure.persistence.adapter;

import com.spotify.playlist_api.domain.model.Playlist;
import com.spotify.playlist_api.domain.model.Song;
import com.spotify.playlist_api.domain.port.PlaylistRepositoryPort;
import com.spotify.playlist_api.infrastructure.persistence.entity.PlaylistEntity;
import com.spotify.playlist_api.infrastructure.persistence.entity.SongEntity;
import com.spotify.playlist_api.infrastructure.persistence.repository.JpaPlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaPlaylistAdapter implements PlaylistRepositoryPort {

    private final JpaPlaylistRepository playlistRepository;

    @Override
    public Playlist save(Playlist playlist) {
        System.out.println("🔄 JpaPlaylistAdapter.save() llamado");
        System.out.println("🔄 Playlist ID: " + playlist.getId());
        System.out.println("🔄 Canciones: " + playlist.getSongs().size());
        try {
            PlaylistEntity entity = toEntity(playlist);
            System.out.println("🔄 Entity creada. ID: " + entity.getId());
            System.out.println("🔄 Entity canciones: " + entity.getCanciones().size());

            // Verificar relación
            if (!entity.getCanciones().isEmpty()) {
                SongEntity firstSong = entity.getCanciones().get(0);
                System.out.println("🔄 Primera canción - ID: " + firstSong.getId());
                System.out.println("🔄 Primera canción - Título: " + firstSong.getTitulo());
                System.out.println("🔄 Primera canción - Playlist: " +
                        (firstSong.getPlaylist() != null ? firstSong.getPlaylist().getId() : "NULL"));
            }

            PlaylistEntity saved = playlistRepository.save(entity);
            System.out.println("✅ Entity guardada en BD");

            return toDomain(saved);

        } catch (Exception e) {
            System.err.println("❌ ERROR en JpaPlaylistAdapter.save()");
            System.err.println("❌ " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Optional<Playlist> findById(UUID id) {
        return playlistRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Playlist> findByNombre(String nombre) {
        return playlistRepository.findByNombre(nombre)
                .map(this::toDomain);
    }

    @Override
    public List<Playlist> findAll() {
        return playlistRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Playlist> findByCreadoPor(String email) {
        return playlistRepository.findByCreadoPor(email).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        playlistRepository.deleteById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return playlistRepository.existsByNombre(nombre);
    }

    private PlaylistEntity toEntity(Playlist playlist) {
        // Crear PlaylistEntity
        PlaylistEntity playlistEntity = PlaylistEntity.builder()
                .nombre(playlist.getName())
                .descripcion(playlist.getDescription())
                .creadoPor(playlist.getCreatedBy())
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdateAt())
                .canciones(new ArrayList<>())
                .build();

        // Crear SongEntities usando los IDs del dominio
        List<SongEntity> songEntities = playlist.getSongs().stream()
                .map(song -> SongEntity.builder()
                        .id(song.getId())  // ← Usar el ID del dominio, no generar nuevo
                        .titulo(song.getTittle())
                        .artista(song.getArtist())
                        .album(song.getAlbum())
                        .anno(song.getYear())
                        .genero(song.getGender())
                        .playlist(playlistEntity)  // ← Establecer relación
                        .build())
                .collect(Collectors.toList());

        playlistEntity.setCanciones(songEntities);
        return playlistEntity;
    }

    private Playlist toDomain(PlaylistEntity entity) {
        List<Song> songs = entity.getCanciones().stream()
                .map(song -> Song.builder()
                        .tittle(song.getTitulo())
                        .artist(song.getArtista())
                        .album(song.getAlbum())
                        .year(song.getAnno())
                        .gender(song.getGenero())
                        .build())
                .collect(Collectors.toList());

        return Playlist.builder()
                .id(entity.getId())
                .name(entity.getNombre())
                .description(entity.getDescripcion())
                .songs(songs)
                .createdBy(entity.getCreadoPor())
                .createdAt(entity.getCreatedAt())
                .updateAt(entity.getUpdatedAt())
                .build();
    }
}
