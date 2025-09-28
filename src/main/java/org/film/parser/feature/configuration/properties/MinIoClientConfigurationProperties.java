package org.film.parser.feature.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("storage.minio.credentials")
public class MinIoClientConfigurationProperties {
    private String endpoint;
    private String name;
    private String password;
}
