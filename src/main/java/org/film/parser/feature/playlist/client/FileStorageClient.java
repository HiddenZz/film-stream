package org.film.parser.feature.playlist.client;

public interface FileStorageClient {
    boolean fileExists(String bucketName, String objectName) throws Exception;
}
