package org.film.parser.feature.movie.repository;

import org.apache.ibatis.annotations.Mapper;
import org.film.parser.feature.movie.data.ContentReady;

@Mapper
public interface ContentReadyRepository {

    void insert(ContentReady contentReady);

    ContentReady findByTmdbId(long tmdbId);
}