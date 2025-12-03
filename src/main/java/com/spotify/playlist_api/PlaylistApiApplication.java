package com.spotify.playlist_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.spotify.playlist_api.infrastructure.persistence.repository")
public class PlaylistApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaylistApiApplication.class, args);
	}

}
