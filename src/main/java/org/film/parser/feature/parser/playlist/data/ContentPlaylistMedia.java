package org.film.parser.feature.parser.playlist.data;

import lombok.Builder;

@Builder
public record ContentPlaylistMedia(String name, byte[] contentPlaylist, String parsedUrl, String resolution) {
}
