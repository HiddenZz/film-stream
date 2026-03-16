package org.film.parser.feature.movie.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.film.parser.feature.movie.data.Movie;

import java.util.List;

@Mapper
public interface MovieRepository {

    void insert(Movie movie);

    Movie findByTmdbId(@Param("tmdbId") long tmdbId);

    List<Movie> findAll(@Param("limit") int limit, @Param("offset") int offset);

    long count();
}
