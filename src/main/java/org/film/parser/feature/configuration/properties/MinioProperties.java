package org.film.parser.feature.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("storage.minio.buckets")
@Configuration
@Data
public class MinioProperties {
    private String defaultBucket;
}
