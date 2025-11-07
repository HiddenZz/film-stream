package org.film.parser.feature.parser.playlist.service;

import org.film.parser.feature.parser.playlist.data.ParsedContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;

public interface PlaylistParserService {

    ParsedMasterMedia parseMasterPlaylist(long id);

    ParsedContentPlaylistMedia parseContentPlaylist(long id, String url, String serviceName);

}
