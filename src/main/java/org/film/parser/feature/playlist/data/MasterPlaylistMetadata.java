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
                             .map(MediaVariant::fallbackUrl)
                             .findFirst()
                             .orElse(null);
    }

    public String getUrlByNormalizedPath(String normalizedPath) {
        return parsedVariants.stream()
                             .filter(variant -> variant.normalizedPath() != null &&
                                              variant.normalizedPath().equals(normalizedPath))
                             .map(MediaVariant::fallbackUrl)
                             .findFirst()
                             .orElse(null);
    }

    public MediaVariant getVariantByNormalizedPath(String normalizedPath) {
        return parsedVariants.stream()
                             .filter(variant -> variant.normalizedPath() != null &&
                                              variant.normalizedPath().equals(normalizedPath))
                             .findFirst()
                             .orElse(null);
    }
}
