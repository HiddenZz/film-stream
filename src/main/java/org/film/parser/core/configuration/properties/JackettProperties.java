package org.film.parser.core.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("torrent.jackett")
public record JackettProperties(String url, String apiKey) {
}
