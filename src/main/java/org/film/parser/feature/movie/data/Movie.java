package org.film.parser.feature.movie.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private Long id;
    private Long tmdbId;
    private String title;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String releaseDate;
    private Double voteAverage;
    private Integer runtime;
}
