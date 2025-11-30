package org.film.parser.feature.parser.playlist.service;

import org.film.parser.core.util.resolver.Named;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;

public interface MasterPlaylistParser extends Named {

    ParsedMasterMedia parse(String iframe, long contentId);

}
