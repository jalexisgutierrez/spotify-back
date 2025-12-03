package com.spotify.playlist_api.infrastructure.controllers;

import com.spotify.playlist_api.application.PlaylistUseCase;
import com.spotify.playlist_api.infrastructure.controllers.dtos.PlaylistRequest;
import com.spotify.playlist_api.infrastructure.controllers.dtos.PlaylistResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistUseCase playlistUseCase;

    /**
     * POST /lists
     * Añadir una nueva lista de reproducción.
     * Si se añade satisfactoriamente, devuelve "201 Created" con la referencia a la URI y el contenido de la lista.
     * Si el nombre de la lista no es válido (ej: null) debe devolver un error "400 Bad Request".
     */
    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            @Valid @RequestBody PlaylistRequest request,
            @AuthenticationPrincipal String userEmail) {

        PlaylistResponse playlist = playlistUseCase.createPlaylist(request, userEmail);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{listName}")
                .buildAndExpand(playlist.getNombre())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(playlist);
    }

    /**
     * GET /lists
     * Ver todas las listas de reproducción existentes.
     */
    @GetMapping
    public ResponseEntity<List<PlaylistResponse>> getAllPlaylists(
            @AuthenticationPrincipal String userEmail) {

        List<PlaylistResponse> playlists = playlistUseCase.getAllPlaylists(userEmail);
        return ResponseEntity.ok(playlists);
    }

    /**
     * GET /lists/{listName}
     * Ver descripción de una lista de reproducción seleccionada.
     * Si la lista no existe, debe devolver un "404 Not Found".
     */
    @GetMapping("/{listName}")
    public ResponseEntity<PlaylistResponse> getPlaylistByName(
            @PathVariable String listName,
            @AuthenticationPrincipal String userEmail) {

        PlaylistResponse playlist = playlistUseCase.getPlaylistByName(listName, userEmail);
        return ResponseEntity.ok(playlist);
    }

    /**
     * DELETE /lists/{listName}
     * Borrar una lista de reproducción.
     * Si se realiza correctamente, debe devolver un "204 No Content".
     * Si la lista no existe debe devolver un "404 Not Found".
     */
    @DeleteMapping("/{listName}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable String listName,
            @AuthenticationPrincipal String userEmail) {

        playlistUseCase.deletePlaylist(listName, userEmail);
        return ResponseEntity.noContent().build();
    }
}
