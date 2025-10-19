package org.film.parser.feature.parser.playlist.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.film.parser.feature.parser.playlist.data.Media;

public interface MasterPlaylistParserService {


    Media parse(String iframe, long contentId);

}
