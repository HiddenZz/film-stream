package org.film.parser.feature.movie.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentReady {

    private Long id;
    private Long tmdbId;
    private String contentUuid;
    private String minioPath;
}