package com.spotify.playlist_api.domain.port;

import java.time.Instant;

public interface TimeProviderPort {
    Instant now();
    long nowMillis();
}
