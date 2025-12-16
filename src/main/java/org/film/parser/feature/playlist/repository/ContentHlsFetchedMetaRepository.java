package org.film.parser.feature.playlist.repository;

import org.film.parser.feature.playlist.data.ContentHlsFetchedMeta;

public interface ContentHlsFetchedMetaRepository {

    int insert(ContentHlsFetchedMeta meta);

    ContentHlsFetchedMeta findById(long id);

}
