package org.film.parser.feature.parser.playlist.data;

import lombok.Builder;

@Builder
public record ParsedContentPlaylistMedia(String name, byte[] contentPlaylist, String hlsUrl) {
}
