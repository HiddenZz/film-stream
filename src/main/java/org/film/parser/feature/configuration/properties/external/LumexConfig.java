package org.film.parser.feature.configuration.properties.external;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

@ConfigurationProperties("parsers.movie.lumex")
@ConfigurationPropertiesBinding
public record LumexConfig(String name, String host) {
}
