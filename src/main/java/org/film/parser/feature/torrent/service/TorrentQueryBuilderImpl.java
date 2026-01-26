package org.film.parser.feature.torrent.service;

import org.film.parser.feature.torrent.data.TMBDMovieInfo;
import org.springframework.stereotype.Component;


@Component
public class TorrentQueryBuilderImpl implements TorrentQueryBuilder {
    @Override
    public String movieSearch(TMBDMovieInfo movie) {
        return "%s".formatted(movie.getTitle());
    }
}
