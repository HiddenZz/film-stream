package org.film.parser.feature.movie.repository;

import org.apache.ibatis.annotations.Mapper;
import org.film.parser.feature.movie.data.ContentReady;

import java.util.List;

@Mapper
public interface ContentReadyRepository {

    void insert(ContentReady contentReady);

    List<ContentReady> findByTmdbId(long tmdbId);
}