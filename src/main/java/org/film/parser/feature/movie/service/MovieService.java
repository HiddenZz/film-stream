package org.film.parser.feature.movie.service;

import org.film.parser.feature.movie.data.ContentReadyEvent;
import org.film.parser.feature.movie.data.MovieStatusResponse;

public interface MovieService {

    void saveReady(ContentReadyEvent event);

    MovieStatusResponse getStatus(long tmdbId);
}