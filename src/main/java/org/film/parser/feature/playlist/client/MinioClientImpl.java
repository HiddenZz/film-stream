package org.film.parser.feature.playlist.client;

import io.minio.MinioClient;
import org.film.parser.feature.configuration.properties.MinioProperties;
import org.springframework.stereotype.Component;

@Component
public class MinioClientImpl implements FileStorageClient {
    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    MinioClientImpl(MinioProperties minioProperties, MinioClient minioClient) {
        this.minioProperties = minioProperties;
        this.minioClient = minioClient;
    }


    @Override
    public boolean fileExists(String bucketName, String objectName) throws Exception {


        return false;
    }



}
