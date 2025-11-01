package org.film.parser.feature.configuration;

import org.film.parser.feature.parser.playlist.service.MasterPlaylistParserResolver;
import org.film.parser.feature.parser.playlist.service.MasterPlaylistParserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CommonConfiguration {

    @Bean
    MasterPlaylistParserResolver masterPlaylistParserResolver(List<MasterPlaylistParserService> masterParsers) {
        return new MasterPlaylistParserResolver(masterParsers);
    }

}
