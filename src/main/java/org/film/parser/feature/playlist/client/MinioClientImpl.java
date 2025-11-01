package org.film.parser.feature.playlist.client;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.configuration.properties.MinioProperties;
import org.film.parser.feature.playlist.data.exceptions.NoSuchFileException;
import org.film.parser.feature.playlist.data.exceptions.SaveFileException;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j
public class MinioClientImpl implements FileStorageClient {
    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    MinioClientImpl(MinioProperties minioProperties, MinioClient minioClient) {
        this.minioProperties = minioProperties;
        this.minioClient = minioClient;
    }


    @Override
    public boolean masterPlaylistExist(String name) {
        final String objectName = generateMasterKey(name);
        try {
            minioClient.statObject(
                    io.minio.StatObjectArgs.builder()
                                           .bucket(minioProperties.topPrefix())
                                           .object(generateMasterKey(name))
                                           .build()
            );

            return true;
        } catch (Exception e) {
            log.error("Error checking if file exists in Minio. Bucket: {}, Object: {}. Error: {}",
                    minioProperties.topPrefix(), objectName, e.getMessage());
            return false;
        }
    }

    @Override
    public InputStream getMasterPlaylist(String name) {
        final String objectName = generateMasterKey(name);
        try {
            return minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                                          .bucket(minioProperties.topPrefix())
                                          .object(generateMasterKey(name))
                                          .build()
            );
        } catch (Exception e) {
            log.error("Error retrieving file from Minio. Bucket: {}, Object: {}. Error: {}",
                    minioProperties.topPrefix(), objectName, e.getMessage());
            throw new NoSuchFileException("No master playlist file in bucket", name, e);
        }


    }

    @Override
    public void saveMasterPlaylist(String name, InputStream inputStream) {
        saveMasterPlaylist(name, minioProperties.hlsName(), inputStream);
    }

    @Override
    public void saveMasterPlaylist(String name, String fileName, InputStream inputStream) {
        final String objectName = generateMasterKey(name);
        try {

            minioClient.putObject(
                    io.minio.PutObjectArgs.builder()
                                          .bucket(minioProperties.topPrefix())
                                          .object(objectName)
                                          .stream(inputStream, -1, 10485760)
                                          .contentType("application/vnd.apple.mpegurl")
                                          .build()
            );
        } catch (Exception e) {
            log.error("Error saving file to Minio. Bucket: {}, Object: {}. Error: {}",
                    minioProperties.topPrefix(), objectName, e.getMessage());
            throw new SaveFileException("Failed to save master playlist file", name, e);
        }
    }


    @Override
    public String generateMasterKey(String name) {
        return generateMasterKey(name, minioProperties.hlsName());
    }

    @Override
    public String generateMasterKey(String name, String fileName) {
        return "%s/%s".formatted(name, fileName);
    }
}
