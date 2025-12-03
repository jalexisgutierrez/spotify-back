package com.spotify.playlist_api.infrastructure.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PlaylistResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private List<SongResponse> canciones;
    private String creadoPor;
    private Instant createdAt;
    private Instant updatedAt;
}

@Data
@Builder
class SongResponse {
    private String titulo;
    private String artista;
    private String album;

    @JsonProperty("year")
    private String year;

    private String genero;
}
