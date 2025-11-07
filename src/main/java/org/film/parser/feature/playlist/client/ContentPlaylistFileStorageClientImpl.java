package org.film.parser.feature.playlist.client;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.configuration.properties.MinioProperties;
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
    public boolean exists(long contentId, int quality) {
        final String mediaKey = generateMediaKey(contentId, quality);

        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                                  .bucket(minioProperties.topPrefix())
                                  .object(mediaKey)
                                  .build()
            );

            return true;
        } catch (Exception e) {
            log.warn("Error checking if file exists in Minio. Bucket: {}, Object: {}. Error: {}",
                    minioProperties.topPrefix(), contentId, e.getMessage());
            return false;
        }
    }

    @Override
    public void save(long contentId, int quality, InputStream inputStream) {
        final String mediaKey = generateMediaKey(contentId, quality);
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(minioProperties.topPrefix())
                                               .stream(inputStream, -1, 10485760)
                                               .contentType("application/vnd.apple.mpegurl")
                                               .build());

        } catch (Exception e) {
            log.error("Error saving file to Minio. Bucket: {}, Object: {}. Error: {}",
                    minioProperties.topPrefix(), contentId, e.getMessage());
            throw new SaveFileException("Failed to save content playlist file", mediaKey, e);
        }
    }

    @Override
    public InputStream get(long contentId, int quality) {
        final String mediaKey = generateMediaKey(contentId, quality);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                                 .bucket(minioProperties.topPrefix())
                                 .object(mediaKey)
                                 .build()
            );
        } catch (Exception e) {
            log.error("Error retrieving file from Minio. Bucket: {}, Object: {}. Error: {}",
                    minioProperties.topPrefix(), mediaKey, e.getMessage());
            throw new NoSuchFileException("No content playlist file in bucket", mediaKey, e);
        }
    }


    private String generateMediaKey(long contentId, int quality) {
        return "%s/%s/%s".formatted(contentId, quality, minioProperties.hlsName());
    }
}
