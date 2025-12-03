package com.spotify.playlist_api.application;

import com.spotify.playlist_api.domain.model.Playlist;
import com.spotify.playlist_api.domain.model.Song;
import com.spotify.playlist_api.domain.port.PlaylistRepositoryPort;
import com.spotify.playlist_api.domain.port.SpotifyClientPort;
import com.spotify.playlist_api.infrastructure.controllers.dtos.PlaylistRequest;
import com.spotify.playlist_api.infrastructure.controllers.dtos.PlaylistResponse;
import com.spotify.playlist_api.infrastructure.controllers.dtos.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlaylistUseCase {

    private final PlaylistRepositoryPort playlistRepository;
    private final SpotifyClientPort spotifyClient;

    public PlaylistResponse createPlaylist(PlaylistRequest request, String userEmail) {
        validatePlaylistRequest(request);

        Playlist playlist = buildPlaylistFromRequest(request, userEmail);
        Playlist saved = playlistRepository.save(playlist);

        return mapToResponse(saved);
    }

    public List<PlaylistResponse> getAllPlaylists(String userEmail) {
        return playlistRepository.findByCreadoPor(userEmail).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PlaylistResponse getPlaylistByName(String listName, String userEmail) {
        return playlistRepository.findByNombre(listName)
                .filter(p -> p.getCreatedBy().equals(userEmail))
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Lista no encontrada: " + listName));
    }

    public void deletePlaylist(String listName, String userEmail) {
        Playlist playlist = playlistRepository.findByNombre(listName)
                .filter(p -> p.getCreatedBy().equals(userEmail))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Lista no encontrada: " + listName));

        playlistRepository.delete(playlist.getId());
    }

    public List<String> getAvailableGenres() {
        return spotifyClient.getAvailableGenres();
    }

    private void validatePlaylistRequest(PlaylistRequest request) {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }

        if (playlistRepository.existsByNombre(request.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una lista con ese nombre");
        }

        if (request.getCanciones() == null || request.getCanciones().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe contener al menos una canción");
        }

        // Validar géneros con Spotify
        List<String> validGenres = spotifyClient.getAvailableGenres();
        request.getCanciones().forEach(song -> {
            if (song.getGenero() == null || !validGenres.contains(song.getGenero().toLowerCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Género no válido: " + song.getGenero());
            }
        });
    }

    private Playlist buildPlaylistFromRequest(PlaylistRequest request, String userEmail) {
        List<Song> songs = request.getCanciones().stream()
                .map(s -> Song.builder()
                        .tittle(s.getTitulo())
                        .artist(s.getArtista())
                        .album(s.getAlbum())
                        .year(s.getYear())
                        .gender(s.getGenero())
                        .build())
                .collect(Collectors.toList());

        return Playlist.builder()
                .name(request.getNombre())
                .description(request.getDescripcion())
                .songs(songs)
                .createdBy(userEmail)
                .createdAt(Instant.now())
                .updateAt(Instant.now())
                .build();
    }

    private PlaylistResponse mapToResponse(Playlist playlist) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .nombre(playlist.getName())
                .descripcion(playlist.getDescription())
                .canciones(playlist.getSongs().stream()
                        .map(s -> SongResponse.builder()
                                .titulo(s.getTittle())
                                .artista(s.getArtist())
                                .album(s.getAlbum())
                                .year(s.getYear())
                                .genero(s.getGender())
                                .build())
                        .collect(Collectors.toList()))
                .creadoPor(playlist.getCreatedBy())
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdateAt())
                .build();
    }
}
