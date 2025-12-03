package com.spotify.playlist_api.domain.port;

import java.util.List;

public interface SpotifyClientPort {
    List<String> getAvailableGenres();
    String getAccessToken();
}
