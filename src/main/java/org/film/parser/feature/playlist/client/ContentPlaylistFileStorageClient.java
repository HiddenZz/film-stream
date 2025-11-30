package org.film.parser.feature.playlist.client;

import java.io.InputStream;

public interface ContentPlaylistFileStorageClient {

    boolean exists(String path);

    void save(String path, InputStream inputStream);

    InputStream get(String path);
}
