package org.film.parser.feature.playlist.service;

import org.film.parser.feature.playlist.data.Playlist;

public interface PlaylistService {

    Playlist getMasterPlaylist(long contentId, String type);

    Playlist getMediaPlaylistByPath(String fullPath, long contentId);

    Playlist getMovie(String fullPath, long contentId, int index);
}
