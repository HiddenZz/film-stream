package org.film.parser.feature.playlist.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.playlist.data.ContentHlsFetchedMeta;
import org.film.parser.feature.playlist.repository.mapper.ContentHlsFetchedMetaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@AllArgsConstructor
public class ContentHlsFetchedMetaRepositoryImpl implements ContentHlsFetchedMetaRepository {

    private final ContentHlsFetchedMetaMapper contentHlsFetchedMetaMapper;

    @Override
    public int insert(ContentHlsFetchedMeta meta) {

        final int id = contentHlsFetchedMetaMapper.insert(meta);

        log.debug("ContentHlsFetchedMeta insertion returned id={}", id);

        return id;
    }

    @Override
    public ContentHlsFetchedMeta findById(long id) {
        final Optional<ContentHlsFetchedMeta> result = contentHlsFetchedMetaMapper.findById(id);

        if (result.isPresent()) {
            log.debug("Found ContentHlsFetchedMeta for id={}", id);
            return result.get();
        }
        log.debug("No ContentHlsFetchedMeta found for id={}", id);

        return null;
    }
}
