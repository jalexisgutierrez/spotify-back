package com.spotify.playlist_api.infrastructure.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistRequest {

    @NotBlank(message = "El nombre de la lista es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "La lista de canciones no puede ser nula")
    @Size(min = 1, message = "Debe haber al menos una canción")
    @Valid
    private List<SongRequest> canciones;
}


