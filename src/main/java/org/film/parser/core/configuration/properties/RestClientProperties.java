package org.film.parser.core.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("network.rest-template")
public class RestClientProperties {
    private String parseHost;
}
