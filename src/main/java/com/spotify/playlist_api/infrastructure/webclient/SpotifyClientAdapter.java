package com.spotify.playlist_api.infrastructure.webclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.spotify.playlist_api.domain.port.SpotifyClientPort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class  SpotifyClientAdapter implements SpotifyClientPort {

    // Usa el WebClient específico para autenticación
    @Qualifier("spotifyAuthWebClient")
    private final WebClient spotifyAuthWebClient;

    // Usa el WebClient específico para la API
    @Qualifier("spotifyApiWebClient")
    private final WebClient spotifyApiWebClient;

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
            log.info("🎵 Obteniendo géneros de Spotify...");

            // Opción 1: Usar endpoint de géneros (puede requerir scopes)
            try {
                Map<String, Object> response = spotifyApiWebClient.get()
                        .uri("/recommendations/available-genre-seeds")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response != null && response.containsKey("genres")) {
                    List<String> genres = (List<String>) response.get("genres");
                    log.info("✅ Géneros obtenidos: {} géneros disponibles", genres.size());
                    return genres;
                }
            } catch (Exception e) {
                log.warn("⚠️ Endpoint /recommendations no disponible: {}", e.getMessage());
            }

            // Opción 2: Usar endpoint de categorías (más confiable)
            try {
                Map<String, Object> response = spotifyApiWebClient.get()
                        .uri("/browse/categories?country=US&limit=50")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (response != null && response.containsKey("categories")) {
                    Map<String, Object> categories = (Map<String, Object>) response.get("categories");
                    if (categories != null && categories.containsKey("items")) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) categories.get("items");
                        List<String> genres = items.stream()
                                .map(item -> (String) item.get("name"))
                                .filter(name -> name != null && !name.trim().isEmpty())
                                .map(String::toLowerCase)  // ← Convertir a minúsculas AQUÍ
                                .distinct()
                                .collect(Collectors.toList());
                        log.info("✅ Categorías obtenidas: {} géneros disponibles", genres.size());
                        return genres;
                    }
                }
            } catch (Exception e) {
                log.warn("⚠️ Endpoint /categories no disponible: {}", e.getMessage());
            }

            // Opción 3: Géneros por defecto
            log.warn("⚠️ Spotify no disponible, usando géneros por defecto");
            return getDefaultGenres();

        } catch (Exception e) {
            log.error("❌ Error obteniendo géneros: {}", e.getMessage());
            return getDefaultGenres();
        }
    }

    @Override
    public String getAccessToken() {
        // Cache por 50 minutos
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiration - 600000) {
            log.debug("♻️ Usando token en caché");
            return cachedToken;
        }

        log.info("🔑 Solicitando nuevo token a Spotify...");

        try {
            // 1. Preparar datos
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "client_credentials");

            // 2. Codificar credenciales
            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            // 3. Hacer solicitud
            Map<String, Object> response = spotifyAuthWebClient.post()
                    .uri("/api/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // 4. Procesar respuesta
            if (response == null) {
                throw new RuntimeException("Respuesta vacía de Spotify");
            }

            if (!response.containsKey("access_token")) {
                log.error("❌ Spotify no devolvió access_token. Respuesta: {}", response);

                // Debug: Mostrar error si existe
                if (response.containsKey("error")) {
                    log.error("❌ Error de Spotify: {} - {}",
                            response.get("error"),
                            response.getOrDefault("error_description", ""));
                }

                throw new RuntimeException("Spotify no devolvió access_token");
            }

            cachedToken = (String) response.get("access_token");
            int expiresIn = (Integer) response.getOrDefault("expires_in", 3600);
            tokenExpiration = System.currentTimeMillis() + (expiresIn * 1000L);

            log.info("✅ Token obtenido! Expira en {} segundos", expiresIn);
            return cachedToken;

        } catch (Exception e) {
            log.error("❌ ERROR obteniendo token: {}", e.getMessage());
            throw new RuntimeException("No se pudo obtener token de Spotify: " + e.getMessage(), e);
        }
    }

    private List<String> getDefaultGenres() {
        return List.of(
                "rock", "pop", "jazz", "classical", "electronic",
                "hip-hop", "reggaeton", "latin", "metal", "blues",
                "country", "folk", "r&b", "soul", "funk", "disco",
                "punk", "indie", "alternative", "k-pop", "reggae"
        );
    }

}
