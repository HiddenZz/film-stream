package org.film.parser.feature.parser.playlist.data;

import lombok.Builder;
import org.film.parser.feature.playlist.data.MediaVariant;

import java.util.List;


@Builder
public record ParsedMasterMedia(String name, byte[] masterPlaylist, String parsedUrl) {
}
