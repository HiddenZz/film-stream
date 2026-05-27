package org.film.parser.feature.torrent.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.film.parser.feature.torrent.data.JackettResult;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class SeedCacheRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<List<JackettResult>> findByMovie(long tmdbId) {
        final String json = redisTemplate.opsForValue().get(movieKey(tmdbId));
        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(json, new TypeReference<List<JackettResult>>() {}));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize cached torrents for tmdbId: %d".formatted(tmdbId), e);
        }
    }

    public void cacheByMovie(long tmdbId, List<JackettResult> torrents, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(movieKey(tmdbId), objectMapper.writeValueAsString(torrents), ttlSeconds, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize torrents for tmdbId: %d".formatted(tmdbId), e);
        }
    }

    String movieKey(long tmdbId) {
        return "movie:torrents:%d".formatted(tmdbId);
    }
}
