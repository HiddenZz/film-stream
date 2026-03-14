package org.film.parser.feature.movie.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContentReadyEvent {

    private long tmdbId;
    private String contentUuid;
    private String minioPath;
}