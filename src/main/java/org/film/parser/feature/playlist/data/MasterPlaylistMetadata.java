package org.film.parser.feature.playlist.data;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record MasterPlaylistMetadata(
        long id,
        long contentId,
        String parserServiceName,
        String masterPlaylistUrl,
        String minioObjectKey,
        StorageStatus status,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
