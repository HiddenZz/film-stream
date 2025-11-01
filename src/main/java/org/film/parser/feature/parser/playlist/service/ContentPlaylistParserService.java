package org.film.parser.feature.parser.playlist.service;

import org.film.parser.feature.parser.playlist.data.ContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;

public interface ContentPlaylistParserService {


    ContentPlaylistMedia parse(ParsedMasterMedia parsedMasterMedia, String resolution);

}
