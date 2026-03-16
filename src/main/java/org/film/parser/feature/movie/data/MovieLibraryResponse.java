package org.film.parser.feature.movie.data;

import java.util.List;

public record MovieLibraryResponse(
        List<MovieSummary> movies,
        long total,
        int offset,
        int limit
) {
}
