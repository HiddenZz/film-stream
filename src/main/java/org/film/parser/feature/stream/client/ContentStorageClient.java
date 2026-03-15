package org.film.parser.feature.stream.client;

import java.io.InputStream;

public interface ContentStorageClient {

    InputStream getObject(String objectKey);
}