package org.film.parser.feature.playlist.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentMediaMeta {
    private long id;
    private long contentId;
    private long contentHlsFetchMetaId;
    private int segmentIndex;
    private String mediaPath;
    private String fallbackUrl;
    private LocalDateTime createdAt;
    private ContentHlsFetchedMeta fetchedMeta;
}
