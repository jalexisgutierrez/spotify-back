package com.spotify.playlist_api.infrastructure.time;

import com.spotify.playlist_api.domain.port.TimeProviderPort;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SystemTimeProvider implements TimeProviderPort {
    @Override
    public Instant now() {
        return Instant.now();
    }

    @Override
    public long nowMillis() {
        return System.currentTimeMillis();
    }
}
