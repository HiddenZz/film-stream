package org.film.parser.feature.parser.playlist.client;

import org.film.parser.feature.parser.playlist.data.AvailablePlayer;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PlaylistRhhParserClientImpl implements PlaylistParserClient {

    private final RestClient restClient;

    PlaylistRhhParserClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<AvailablePlayer> moviePlaylist(long movieId) {

        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>() {
            {
                add("kinopoisk", String.valueOf(movieId));
                add("type", "movie");
            }
        };

        try {
            final AvailablePlayer[] availablePlayers = restClient.post()
                                                                 .uri("/cache")
                                                                 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                                 .body(formData)
                                                                 .retrieve()
                                                                 .toEntity(AvailablePlayer[].class)
                                                                 .getBody();
            if (availablePlayers == null) {
                return List.of();
            }

            return List.of(availablePlayers);
        } catch (final Exception e) {
            return List.of();
        }


    }

}
