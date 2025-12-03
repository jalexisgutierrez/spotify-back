package com.spotify.playlist_api.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class Playlist {
    UUID id;
    String name;
    String description;
    List<Song> songs;
    String createdBy;
    Instant createdAt;
    Instant updateAt;
}
