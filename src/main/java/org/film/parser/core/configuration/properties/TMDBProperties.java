package org.film.parser.core.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tmdb")
public record TMDBProperties(String baseUrl, String apiToken) {
}
