package org.film.parser.feature.parser.playlist.client;

import org.film.parser.feature.parser.playlist.data.AvailablePlayer;

import java.util.List;

public interface PlaylistParserClient {

    List<AvailablePlayer> moviePlaylist(long movieId);

}
