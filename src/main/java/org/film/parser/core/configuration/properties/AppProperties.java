package org.film.parser.core.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record AppProperties(String publicBaseUrl) {
}
