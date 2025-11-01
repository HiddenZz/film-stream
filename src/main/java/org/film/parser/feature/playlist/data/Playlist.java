package org.film.parser.feature.playlist.data;

import org.springframework.core.io.Resource;

import java.io.InputStream;

public record Playlist(
        Resource content
) {
}
