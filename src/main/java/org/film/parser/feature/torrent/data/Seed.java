package org.film.parser.feature.torrent.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seed {

    private long tmdbId;
    private String guid;
    private String title;
    private String externalLink;
}
