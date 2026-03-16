package org.film.parser.feature.stream.client;

import java.io.InputStream;

public interface ContentStorageClient {

    InputStream getObject(String objectKey);

    void putObject(String objectKey, InputStream data, long size, String contentType);
}