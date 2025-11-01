package org.film.parser.feature.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

import java.util.List;


@ConfigurationProperties("storage.minio.buckets")
@ConfigurationPropertiesBinding
public record MinioProperties(String topPrefix, String hlsName) {
}
