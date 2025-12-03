package com.spotify.playlist_api.infrastructure.controllers;

import com.spotify.playlist_api.application.PlaylistUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final PlaylistUseCase playlistUseCase;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAvailableGenres() {
        List<String> genres = playlistUseCase.getAvailableGenres();
        return ResponseEntity.ok(Map.of(
                "genres", genres,
                "count", genres.size(),
                "source", "Spotify API"
        ));
    }
}
