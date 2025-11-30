package org.film.parser.feature.configuration;

import org.film.parser.core.util.resolver.ServiceResolver;
import org.film.parser.feature.parser.playlist.service.ContentPlaylistParserService;
import org.film.parser.feature.parser.playlist.service.MasterPlaylistParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CommonConfiguration {

    @Bean
    ServiceResolver<MasterPlaylistParser> masterPlaylistParserResolver(
            List<MasterPlaylistParser> masterParsers) {
        return new ServiceResolver<>(masterParsers);
    }

    @Bean
    ServiceResolver<ContentPlaylistParserService> contentPlaylistParserResolver(
            List<ContentPlaylistParserService> contentParsers) {
        return new ServiceResolver<>(contentParsers);
    }

}
