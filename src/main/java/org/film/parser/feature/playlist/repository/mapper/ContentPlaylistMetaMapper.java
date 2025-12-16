package org.film.parser.feature.playlist.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.film.parser.feature.playlist.data.ContentMediaMeta;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ContentPlaylistMetaMapper {

    int insertAll(List<ContentMediaMeta> list);

    Optional<ContentMediaMeta> findByInfo(long id, int index, String mediaPath);


}
