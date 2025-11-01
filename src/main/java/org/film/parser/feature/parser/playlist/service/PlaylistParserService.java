package org.film.parser.feature.parser.playlist.service;

import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;

public interface PlaylistParserService {

    ParsedMasterMedia parseMasterPlaylist(long id);

}
