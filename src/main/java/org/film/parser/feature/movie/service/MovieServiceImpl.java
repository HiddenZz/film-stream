package org.film.parser.feature.movie.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.movie.data.ContentReady;
import org.film.parser.feature.movie.data.ContentReadyEvent;
import org.film.parser.feature.movie.data.MovieStatusResponse;
import org.film.parser.feature.movie.repository.ContentReadyRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final ContentReadyRepository repository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveReady(ContentReadyEvent event) {
        final ContentReady entity = new ContentReady();
        entity.setTmdbId(event.getTmdbId());
        entity.setContentUuid(event.getContentUuid());
        entity.setMinioPath(event.getMinioPath());
        repository.insert(entity);
        log.info("Movie ready saved: tmdbId={}, path={}", event.getTmdbId(), event.getMinioPath());
    }

    @Override
    public MovieStatusResponse getStatus(long tmdbId) {
        final ContentReady ready = repository.findByTmdbId(tmdbId);
        if (ready != null) {
            return MovieStatusResponse.ready(ready.getMinioPath());
        }

        final boolean isProcessing = isProcessing(tmdbId);
        return isProcessing ? MovieStatusResponse.processing() : MovieStatusResponse.notFound();
    }

    private boolean isProcessing(long tmdbId) {
        final Boolean downloading = redisTemplate.hasKey("progress:DOWNLOADING:%d".formatted(tmdbId));
        final Boolean formatting = redisTemplate.hasKey("progress:FORMATTING:%d".formatted(tmdbId));
        return downloading || formatting;
    }
}