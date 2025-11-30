package org.film.parser.feature.playlist.data;

import lombok.Builder;

import java.util.List;

@Builder
public record ContentPlaylistMedia(byte[] content, List<ContentMediaMeta> contentVariants) {
}
