package org.film.parser.feature.parser.playlist.client;

import org.film.parser.feature.parser.playlist.data.AvailablePlayer;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PlaylistRhhParserClientImpl implements PlaylistParserClient {

    private final RestTemplate restTemplate;

    PlaylistRhhParserClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<AvailablePlayer> moviePlaylist(long movieId) {

        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>(){
            {
                add("kinopoisk", String.valueOf(movieId));
                add("type", "movie");
            }
        };

        final HttpHeaders headers = new HttpHeaders(){
            {
                setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            }
        };

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        final AvailablePlayer[] availablePlayers = restTemplate.postForObject("/cache", request, AvailablePlayer[].class);

        if(availablePlayers == null) {
            return List.of();
        }

        return List.of(availablePlayers);
    }

}
