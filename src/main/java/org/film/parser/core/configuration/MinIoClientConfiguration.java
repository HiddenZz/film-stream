package org.film.parser.core.configuration;

import io.minio.MinioClient;
import org.film.parser.core.configuration.properties.MinIoClientProperties;
import org.film.parser.core.configuration.properties.MinioProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({MinIoClientProperties.class, MinioProperties.class})
@Configuration
public class MinIoClientConfiguration {

    @Bean
    MinioClient minioClient(MinIoClientProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getName(), properties.getPassword())
                .build();
    }

    ;
}
