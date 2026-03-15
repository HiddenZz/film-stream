package org.film.parser.feature.movie.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.movie.data.ContentReady;
import org.film.parser.feature.movie.data.ContentReadyEvent;
import org.film.parser.feature.movie.data.ContentVersion;
import org.film.parser.feature.movie.data.MovieStatusResponse;
import org.film.parser.feature.movie.repository.ContentReadyRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final ContentReadyRepository repository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveReady(ContentReadyEvent event) {
        final ContentReady entity = ContentReady.builder()
                .tmdbId(event.tmdbId())
                .contentUuid(event.contentUuid())
                .minioPath(event.minioPath())
                .build();
        repository.insert(entity);
        log.info("Movie ready saved: tmdbId={}, contentUuid={}", event.tmdbId(), event.contentUuid());
    }

    @Override
    public MovieStatusResponse getStatus(long tmdbId) {
        final List<ContentReady> versions = repository.findByTmdbId(tmdbId);
        if (!versions.isEmpty()) {
            final List<ContentVersion> contentVersions = versions.stream()
                    .map(v -> new ContentVersion(v.getContentUuid(), v.getMinioPath()))
                    .toList();
            return MovieStatusResponse.ready(contentVersions);
        }

        return isProcessing(tmdbId) ? MovieStatusResponse.processing() : MovieStatusResponse.notFound();
    }

    private boolean isProcessing(long tmdbId) {
        final Boolean downloading = redisTemplate.hasKey("progress:DOWNLOADING:%d".formatted(tmdbId));
        final Boolean formatting = redisTemplate.hasKey("progress:FORMATTING:%d".formatted(tmdbId));
        return downloading || formatting;
    }
}