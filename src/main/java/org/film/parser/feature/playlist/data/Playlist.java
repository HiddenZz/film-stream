package org.film.parser.feature.playlist.data;

import org.springframework.core.io.Resource;


public record Playlist(
        Resource content
) {
}
