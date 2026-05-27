package org.film.parser.feature.torrent.service;

import org.film.parser.feature.torrent.data.JackettResult;
import org.film.parser.feature.torrent.data.Seed;

import java.util.List;

public interface TorrentService {

    List<Seed> searchSeeds(long tmdbId);

    JackettResult getTorrent(long tmdbId, String guid);

    String requestDownload(long tmdbId, String guid);
}
