package org.film.parser.feature.stream.client;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.AllArgsConstructor;
import org.film.parser.core.configuration.properties.MinioProperties;
import org.film.parser.core.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@AllArgsConstructor
public class MinioContentStorageClient implements ContentStorageClient {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public InputStream getObject(String objectKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.topPrefix())
                            .object(objectKey)
                            .build()
            );
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new ResourceNotFoundException("Object not found: " + objectKey);
            }
            throw new RuntimeException("MinIO error", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read from MinIO", e);
        }
    }

    @Override
    public void putObject(String objectKey, InputStream data, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.topPrefix())
                            .object(objectKey)
                            .stream(data, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to write to MinIO: " + objectKey, e);
        }
    }
}