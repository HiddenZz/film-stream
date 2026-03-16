package org.film.parser.feature.movie.data;

import lombok.Builder;

@Builder
public record MovieSummary(
        long tmdbId,
        String title,
        String overview,
        String posterUrl,
        String releaseDate,
        Double voteAverage,
        Integer runtime
) {
}
