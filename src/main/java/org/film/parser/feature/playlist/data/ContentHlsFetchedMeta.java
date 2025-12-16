package org.film.parser.feature.playlist.data;

import lombok.Builder;

@Builder
public record ContentHlsFetchedMeta(
        long id,
        long contentId,
        String url,
        String serviceName
) {
}
