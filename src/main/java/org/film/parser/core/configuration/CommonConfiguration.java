package org.film.parser.core.configuration;

import org.film.parser.feature.torrent.client.TMDBClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class CommonConfiguration {

    @Bean
    TMDBClient tmdbClient(RestClient restClient) {
        return new TMDBClient(restClient);
    }
}
