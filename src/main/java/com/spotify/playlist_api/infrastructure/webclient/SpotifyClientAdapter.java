package com.spotify.playlist_api.infrastructure.webclient;

import com.spotify.playlist_api.domain.port.SpotifyClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class  SpotifyClientAdapter implements SpotifyClientPort {

    private final WebClient webClient;

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.token-url}")
    private String tokenUrl;

    @Value("${spotify.genres-url}")
    private String genresUrl;

    private String cachedToken;
    private long tokenExpiration;

    @Override
    public List<String> getAvailableGenres() {
        try {
            String token = getAccessToken();

            Map<String, Object> response = webClient.get()
                    .uri(genresUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("genres")) {
                return (List<String>) response.get("genres");
            }
            return List.of("pop", "rock", "jazz", "classical", "hip-hop");

        } catch (Exception e) {
            log.warn("Error obteniendo géneros de Spotify, usando lista por defecto", e);
            return List.of("pop", "rock", "jazz", "classical", "hip-hop", "electronic", "reggae", "blues");
        }
    }

    @Override
    public String getAccessToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiration) {
            return cachedToken;
        }

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        Map<String, String> request = Map.of("grant_type", "client_credentials");

        Map<String, Object> response = webClient.post()
                .uri(tokenUrl)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && response.containsKey("access_token")) {
            cachedToken = (String) response.get("access_token");
            int expiresIn = (Integer) response.getOrDefault("expires_in", 3600);
            tokenExpiration = System.currentTimeMillis() + (expiresIn * 1000);
            return cachedToken;
        }

        throw new RuntimeException("No se pudo obtener token de Spotify");
    }

}
