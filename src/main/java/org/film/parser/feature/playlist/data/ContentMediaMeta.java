package org.film.parser.feature.playlist.data;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContentMediaMeta(
        long id,
        long contentId,
        int segmentIndex,
        String mediaPath,
        String fallbackUrl,
        LocalDateTime createdAt
) {
}
