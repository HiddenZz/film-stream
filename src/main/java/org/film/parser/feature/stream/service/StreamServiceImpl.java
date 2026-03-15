package org.film.parser.feature.stream.service;

import lombok.AllArgsConstructor;
import org.film.parser.core.exception.ResourceNotFoundException;
import org.film.parser.feature.movie.data.ContentReady;
import org.film.parser.feature.movie.repository.ContentReadyRepository;
import org.film.parser.feature.stream.client.ContentStorageClient;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@AllArgsConstructor
public class StreamServiceImpl implements StreamService {

    private final ContentStorageClient storageClient;
    private final ContentReadyRepository repository;

    @Override
    public InputStream openMaster(String contentUuid) {
        return storageClient.getObject(resolveMinioPath(contentUuid));
    }

    @Override
    public InputStream openPlaylist(String contentUuid, String quality) {
        String base = basePath(resolveMinioPath(contentUuid));
        return storageClient.getObject(base + "/" + quality + "/playlist.m3u8");
    }

    @Override
    public InputStream openSegment(String contentUuid, String quality, String segment) {
        String base = basePath(resolveMinioPath(contentUuid));
        return storageClient.getObject(base + "/" + quality + "/" + segment);
    }

    private String resolveMinioPath(String contentUuid) {
        ContentReady content = repository.findByContentUuid(contentUuid);
        if (content == null) {
            throw new ResourceNotFoundException("Content not found: " + contentUuid);
        }
        return content.getMinioPath();
    }

    private String basePath(String minioPath) {
        return minioPath.replace("/master.m3u8", "");
    }
}