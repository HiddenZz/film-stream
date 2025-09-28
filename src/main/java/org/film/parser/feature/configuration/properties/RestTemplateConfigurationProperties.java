package org.film.parser.feature.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("network.rest-template")
public class RestTemplateConfigurationProperties {
    private String parseHost;
}
