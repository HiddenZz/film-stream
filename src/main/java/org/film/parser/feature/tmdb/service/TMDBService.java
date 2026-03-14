package org.film.parser.feature.tmdb.service;

import org.film.parser.feature.tmdb.data.TMDBMovieDetails;
import org.film.parser.feature.tmdb.data.TMDBSearchResponse;

public interface TMDBService {

    TMDBSearchResponse searchMovies(String query, String language, int page);

    TMDBMovieDetails movieDetails(long id, String language);
}