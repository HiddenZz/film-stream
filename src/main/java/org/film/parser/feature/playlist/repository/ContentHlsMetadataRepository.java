package org.film.parser.feature.playlist.repository;

import org.film.parser.feature.playlist.data.ContentMediaMeta;

import java.util.List;
import java.util.Optional;

public interface ContentHlsMetadataRepository {

    void saveAll(List<ContentMediaMeta> metaList);

    Optional<ContentMediaMeta> findByInfo(int contentId, int segmentIndex, String mediaPath);
}
