package org.film.parser.core.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

@ConfigurationProperties("torrent.jackett")
public record JackettProperties(String url, String apiKey) {
}
