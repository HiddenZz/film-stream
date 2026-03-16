package org.film.parser.feature.movie.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ProgressRepository {

    private final StringRedisTemplate redisTemplate;

    public boolean isProcessing(long tmdbId) {
        Boolean downloading = redisTemplate.hasKey("progress:DOWNLOADING:%d".formatted(tmdbId));
        Boolean formatting = redisTemplate.hasKey("progress:FORMATTING:%d".formatted(tmdbId));
        return downloading || formatting;
    }
}
