package org.film.parser.feature.movie.data;

public record ContentReadyEvent(long tmdbId, String contentUuid, String minioPath) {
}