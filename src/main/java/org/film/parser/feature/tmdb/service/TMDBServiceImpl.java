package org.film.parser.feature.tmdb.service;

import lombok.AllArgsConstructor;
import org.film.parser.feature.tmdb.client.TMDBClient;
import org.film.parser.feature.tmdb.data.TMDBMovieDetails;
import org.film.parser.feature.tmdb.data.TMDBSearchResponse;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TMDBServiceImpl implements TMDBService {

    private final TMDBClient tmdbClient;

    @Override
    public TMDBSearchResponse searchMovies(String query, String language, int page) {
        return tmdbClient.searchMovies(query, language, page);
    }

    @Override
    public TMDBMovieDetails movieDetails(long id, String language) {
        return tmdbClient.movieDetails(id, language);
    }
}