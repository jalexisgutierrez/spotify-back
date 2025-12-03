package com.spotify.playlist_api.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Song {
    String tittle;
    String artist;
    String album;
    String year;
    String gender;
}
