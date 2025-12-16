package org.film.parser.feature.playlist.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.playlist.data.ContentMediaMeta;
import org.film.parser.feature.playlist.repository.mapper.ContentPlaylistMetaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@AllArgsConstructor
public class ContentHlsMetadataRepositoryImpl implements ContentHlsMetadataRepository {

    final ContentPlaylistMetaMapper contentPlaylistMetaMapper;


    @Override
    public void saveAll(List<ContentMediaMeta> metaList) {
        final int inserted = contentPlaylistMetaMapper.insertAll(metaList);
        if (inserted != metaList.size()) {
            log.error("Mismatch in inserted rows: expected {}, but got {}", metaList.size(), inserted);
            throw new IllegalStateException("Failed to insert all content media metadata");
        }

        log.debug("Successfully inserted {} content media metadata records", inserted);
    }

    @Override
    public ContentMediaMeta findByInfo(long contentId, int segmentIndex, String mediaPath) {
        final Optional<ContentMediaMeta> result =
                contentPlaylistMetaMapper.findByInfo(contentId, segmentIndex, mediaPath);
        if (result.isPresent()) {
            log.debug("Found content media metadata for contentId={}, segmentIndex={}, mediaPath={}",
                    contentId, segmentIndex, mediaPath);
            return result.get();
        }

        log.debug("No content media metadata found for contentId={}, segmentIndex={}, mediaPath={}",
                contentId, segmentIndex, mediaPath);

        throw new IllegalStateException("Content media metadata not found");
    }
}
