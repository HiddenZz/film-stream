package org.film.parser.feature.movie.service;

import org.film.parser.feature.movie.data.ContentReadyEvent;
import org.film.parser.feature.movie.data.MovieLibraryResponse;
import org.film.parser.feature.movie.data.MovieStatusResponse;

import java.io.InputStream;

public interface MovieService {

    void saveReady(ContentReadyEvent event);

    MovieStatusResponse getStatus(long tmdbId);

    MovieLibraryResponse getLibrary(int offset, int limit);

    InputStream getPoster(long tmdbId);
}