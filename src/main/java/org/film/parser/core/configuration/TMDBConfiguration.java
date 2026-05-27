package org.film.parser.core.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.film.parser.core.configuration.properties.TMDBProperties;
import org.film.parser.feature.tmdb.client.TMDBClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties({TMDBProperties.class})
@Configuration
public class TMDBConfiguration {

    @Bean
    public TMDBClient tmdbClient(TMDBProperties properties, RestClient restClient) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final RestClient tmdbRestClient = restClient.mutate()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.apiToken())
                .messageConverters(converters -> {
                    converters.addFirst(new MappingJackson2HttpMessageConverter(objectMapper));
                })
                .build();

        return new TMDBClient(tmdbRestClient);
    }
}