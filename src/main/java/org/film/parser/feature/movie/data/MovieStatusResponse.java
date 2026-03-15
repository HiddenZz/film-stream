package org.film.parser.feature.movie.data;

import java.util.List;

public record MovieStatusResponse(MovieStatus status, List<ContentVersion> versions) {

    public static MovieStatusResponse ready(List<ContentVersion> versions) {
        return new MovieStatusResponse(MovieStatus.READY, versions);
    }

    public static MovieStatusResponse processing() {
        return new MovieStatusResponse(MovieStatus.PROCESSING, null);
    }

    public static MovieStatusResponse notFound() {
        return new MovieStatusResponse(MovieStatus.NOT_FOUND, null);
    }
}