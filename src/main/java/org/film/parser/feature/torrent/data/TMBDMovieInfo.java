package org.film.parser.feature.torrent.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TMBDMovieInfo {

    private String title;
    private String year;
}
