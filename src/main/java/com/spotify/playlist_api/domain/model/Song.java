package com.spotify.playlist_api.domain.model;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Song {
    private UUID id;
    String tittle;
    String artist;
    String album;
    String year;
    String gender;
}
