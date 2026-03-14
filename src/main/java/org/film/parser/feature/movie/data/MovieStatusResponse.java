package org.film.parser.feature.movie.data;

public record MovieStatusResponse(MovieStatus status, String minioPath) {

    public static MovieStatusResponse ready(String minioPath) {
        return new MovieStatusResponse(MovieStatus.READY, minioPath);
    }

    public static MovieStatusResponse processing() {
        return new MovieStatusResponse(MovieStatus.PROCESSING, null);
    }

    public static MovieStatusResponse notFound() {
        return new MovieStatusResponse(MovieStatus.NOT_FOUND, null);
    }
}