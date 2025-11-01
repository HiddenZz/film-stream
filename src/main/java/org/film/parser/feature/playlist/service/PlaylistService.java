package org.film.parser.feature.playlist.service;

import org.film.parser.feature.playlist.data.Playlist;

public interface PlaylistService {

    Playlist getPlaylist(long contentId, String type);

}
