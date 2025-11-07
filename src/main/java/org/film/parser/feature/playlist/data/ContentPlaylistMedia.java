package org.film.parser.feature.playlist.data;

import lombok.Builder;

@Builder
public record ContentPlaylistMedia(byte[] content, int quality) {
}
