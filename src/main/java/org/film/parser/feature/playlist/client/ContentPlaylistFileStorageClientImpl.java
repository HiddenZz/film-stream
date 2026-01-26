package org.film.parser.feature.playlist.client;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.configuration.properties.MinioProperties;
import org.film.parser.feature.playlist.data.exceptions.NoSuchFileException;
import org.film.parser.feature.playlist.data.exceptions.SaveFileException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
public class ContentPlaylistFileStorageClientImpl implements ContentPlaylistFileStorageClient {

    final MinioClient minioClient;
    final MinioProperties minioProperties;

    public ContentPlaylistFileStorageClientImpl(MinioClient minioClient,
                                                MinioProperties minioProperties
    ) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public boolean exists(String path) {

        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.topPrefix())
                            .object(path)
                            .build()
            );

            return true;
        } catch (Exception e) {
            log.warn("Error checking if file exists in Minio. Bucket: {}, Object: {}. Error: {}",
                     minioProperties.topPrefix(), path, e.getMessage());
            return false;
        }
    }

    @Override
    public void save(String path, InputStream inputStream) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                                          .bucket(minioProperties.topPrefix())
                                          .object(path)
                                          .stream(inputStream, -1, 10485760)
                                          .build());

        } catch (Exception e) {
            log.error("Error saving file to Minio. Bucket: {}, Object: {}. Error: {}",
                      minioProperties.topPrefix(), path, e.getMessage());
            throw new SaveFileException("Failed to save content playlist file", path, e);
        }
    }

    @Override
    public InputStream get(String path) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.topPrefix())
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error retrieving file from Minio. Bucket: {}, Object: {}. Error: {}",
                      minioProperties.topPrefix(), path, e.getMessage());
            throw new NoSuchFileException("No content playlist file in bucket", path, e);
        }
    }


}
