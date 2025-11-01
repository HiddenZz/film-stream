package org.film.parser.feature.parser.playlist.service;

import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;

public interface MasterPlaylistParserService {

    ParsedMasterMedia parse(String iframe, long contentId);

    String getName();
}
