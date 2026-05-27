package org.film.parser.feature.tmdb.service;

import lombok.AllArgsConstructor;
import org.film.parser.feature.tmdb.client.TMDBClient;
import org.film.parser.feature.tmdb.data.TMDBMovieDetails;
import org.film.parser.feature.tmdb.data.TMDBMoviePreview;
import org.film.parser.feature.tmdb.data.TMDBSearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.URIParameter;
import java.util.List;

@Service
@AllArgsConstructor
public class TMDBServiceImpl implements TMDBService {

    private final TMDBClient tmdbClient;

    @Override
    public TMDBSearchResponse searchMovies(String query, String language, int page) {
        final TMDBSearchResponse response = tmdbClient.searchMovies(query, language, page);
        response.setResults(addHostToPosters(response.getResults()));
        return response;
    }

    @Override
    public TMDBMovieDetails movieDetails(long id, String language) {
        return tmdbClient.movieDetails(id, language);
    }


    private List<TMDBMoviePreview> addHostToPosters(List<TMDBMoviePreview> movies) {
        return movies.stream()
                .peek(movie -> movie.setPosterPath(UriComponentsBuilder.fromUriString("https://image.tmdb.org/t/p/w500/")
                                                           .path(movie.getPosterPath())
                                                           .toUriString())).toList();
    }
}