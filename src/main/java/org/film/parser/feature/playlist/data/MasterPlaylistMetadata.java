package org.film.parser.feature.playlist.data;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;


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
        LocalDateTime updatedAt,
        List<MediaVariant> parsedVariants
) {


    public String getUrlByQuality(int quality) {
        return parsedVariants.stream()
                             .filter(variant -> variant.resolution() == quality)
                             .map(MediaVariant::path)
                             .findFirst()
                             .orElse(null);
    }
}
