package org.film.parser.feature.parser.playlist.data;

import lombok.Builder;


@Builder
public record ParsedMasterMedia(String name, byte[] masterPlaylist, String parsedUrl) {
}
