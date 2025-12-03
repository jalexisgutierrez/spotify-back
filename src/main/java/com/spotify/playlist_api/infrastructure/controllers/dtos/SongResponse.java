package com.spotify.playlist_api.infrastructure.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SongResponse {
    private String titulo;
    private String artista;
    private String album;

    @JsonProperty("year")
    private String year;

    private String genero;
}
