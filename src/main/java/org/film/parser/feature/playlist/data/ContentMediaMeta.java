package org.film.parser.feature.playlist.data;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContentMediaMeta(
        int id,
        int playlistId,
        int quality,
        int segmentIndex,
        String segmentUrl,
        double durationSeconds,
        double offsetSeconds,
        LocalDateTime createdAt
) {
}
