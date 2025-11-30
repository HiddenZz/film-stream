package org.film.parser.feature.parser.playlist.service;

import org.film.parser.core.util.resolver.Named;
import org.film.parser.feature.parser.playlist.data.ParsedContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;

public interface ContentPlaylistParserService extends Named {

    ParsedContentPlaylistMedia parse(String masterHlsUrl, String url);

}
