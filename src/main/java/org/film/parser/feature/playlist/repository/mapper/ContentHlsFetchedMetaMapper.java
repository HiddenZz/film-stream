package org.film.parser.feature.playlist.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.film.parser.feature.playlist.data.ContentHlsFetchedMeta;

import java.util.Optional;

@Mapper
public interface ContentHlsFetchedMetaMapper {

    int insert(ContentHlsFetchedMeta meta);

    Optional<ContentHlsFetchedMeta> findById(long id);
}
