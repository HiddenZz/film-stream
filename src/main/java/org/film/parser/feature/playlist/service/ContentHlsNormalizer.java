package org.film.parser.feature.playlist.service;

import org.film.parser.feature.playlist.data.ContentPlaylistMedia;
import org.film.parser.feature.playlist.data.MediaVariant;

public interface ContentHlsNormalizer {

    ContentPlaylistMedia normalize(byte[] media, MediaVariant mediaVariant, long contentId);

}
