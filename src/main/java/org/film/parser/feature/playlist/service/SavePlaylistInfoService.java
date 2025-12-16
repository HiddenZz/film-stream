package org.film.parser.feature.playlist.service;

import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.playlist.data.ContentHlsFetchedMeta;
import org.film.parser.feature.playlist.data.ContentPlaylistMedia;
import org.film.parser.feature.playlist.data.MasterMedia;

public interface SavePlaylistInfoService {

    void saveMasterPlaylistInfo(long contentId, MasterMedia parsedMasterMedia);

    void saveContentPlaylistInfo(String path, ContentHlsFetchedMeta fetchedMeta,
                                 ContentPlaylistMedia contentPlaylistMedia);
}
