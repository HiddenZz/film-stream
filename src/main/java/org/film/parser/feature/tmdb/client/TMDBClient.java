package org.film.parser.feature.tmdb.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.exception.ResourceNotFoundException;
import org.film.parser.feature.tmdb.data.TMDBMovieDetails;
import org.film.parser.feature.tmdb.data.TMDBSearchResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@AllArgsConstructor
public class TMDBClient {

    private final RestClient restClient;

    public TMDBSearchResponse searchMovies(String query, String language, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("query", query)
                        .queryParam("language", language)
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .body(TMDBSearchResponse.class);
    }

    public TMDBMovieDetails movieDetails(long id, String language) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("language", language)
                        .build(id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ResourceNotFoundException("Movie not found in TMDB: %d".formatted(id));
                })
                .body(TMDBMovieDetails.class);
    }


}