package org.film.parser.feature.torrent.service;

import org.film.parser.feature.torrent.data.JackettResult;
import org.film.parser.feature.torrent.data.Seed;

import java.util.List;

public interface LocalCacheSeedsService {

    List<Seed> sendTorrents(List<JackettResult> jackettResults);


    JackettResult getTorrent(String guid);

}
