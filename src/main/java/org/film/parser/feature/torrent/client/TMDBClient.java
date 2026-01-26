package org.film.parser.feature.torrent.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.torrent.data.TMBDMovieInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@AllArgsConstructor
public class TMDBClient {

    private final RestClient restClient;

    TMBDMovieInfo movie(long id){
        return restClient.get().uri("movie/%d".formatted(id)).retrieve().toEntity(TMBDMovieInfo.class).getBody();
    }
}
