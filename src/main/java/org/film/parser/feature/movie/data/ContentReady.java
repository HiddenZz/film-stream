package org.film.parser.feature.movie.data;

import lombok.Data;

@Data
public class ContentReady {

    private Long id;
    private Long tmdbId;
    private String contentUuid;
    private String minioPath;
}