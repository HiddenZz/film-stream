package org.film.parser.feature.playlist.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MediaVariant(
        MediaType mediaType,
        int resolution,
        String fallbackUrl,
        String normalizedPath,
        MediaMetadata metadata
) {
    public MediaVariant(int resolution, String fallbackUrl) {
        this(MediaType.VIDEO, resolution, fallbackUrl, String.valueOf(resolution), null);
    }

    public enum MediaType {
        VIDEO,
        AUDIO,
        SUBTITLES,
        CLOSED_CAPTIONS,
        IFRAME
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record MediaMetadata(
            String groupId,
            String name,
            String language,
            Boolean isDefault,
            String codecs,
            Long bandwidth
    ) {
    }
}
