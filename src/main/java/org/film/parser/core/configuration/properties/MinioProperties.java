package org.film.parser.core.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("storage.minio.buckets")
public record MinioProperties(String topPrefix, String hlsName) {
}
