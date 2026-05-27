package org.film.parser.feature.tmdb.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TMDBSearchResponse {

    private int page;
    private List<TMDBMoviePreview> results;
    private int totalPages;
    private int totalResults;
}