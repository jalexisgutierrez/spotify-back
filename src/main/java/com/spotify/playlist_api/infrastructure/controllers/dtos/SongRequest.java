package com.spotify.playlist_api.infrastructure.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SongRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El artista es obligatorio")
    private String artista;

    @NotBlank(message = "El álbum es obligatorio")
    private String album;

    @NotBlank(message = "El año es obligatorio")
    @JsonProperty("year")
    private String year;

    @NotBlank(message = "El género es obligatorio")
    private String genero;
}
