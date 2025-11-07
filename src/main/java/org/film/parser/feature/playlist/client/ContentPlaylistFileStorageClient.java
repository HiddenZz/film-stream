package org.film.parser.feature.playlist.client;

import java.io.InputStream;

public interface ContentPlaylistFileStorageClient {

    boolean exists(long contentId, int quality);

    void save(long contentId, int quality, InputStream inputStream);

    InputStream get(long contentId, int quality);
}
