package org.film.parser.feature.configuration;

import io.minio.MinioClient;
import org.film.parser.feature.configuration.properties.MinIoClientConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(MinIoClientConfigurationProperties.class)
@Configuration
public class MinIoClientConfiguration {

    @Bean
    MinioClient minioClient(MinIoClientConfigurationProperties properties) {
        return MinioClient.builder()
                  .endpoint(properties.getEndpoint())
                  .credentials(properties.getName(), properties.getPassword())
                  .build();
    };
}
