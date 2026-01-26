package org.film.parser.feature.torrent.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Seed {

    private String guid;
    private String title;
    private String externalLink;
}
