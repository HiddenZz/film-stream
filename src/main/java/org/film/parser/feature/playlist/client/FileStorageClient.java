package org.film.parser.feature.playlist.client;

import java.io.InputStream;

public interface FileStorageClient {
    boolean masterPlaylistExist(String name);

    InputStream getMasterPlaylist(String name);

    void saveMasterPlaylist(String name, InputStream inputStream);

    void saveMasterPlaylist(String name, String fileName, InputStream inputStream);

    String generateMasterKey(String name, String fileName);

    String generateMasterKey(String name);

}
