package org.film.parser.feature.torrent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.film.parser.core.configuration.properties.RedisProperties;
import org.film.parser.core.exception.ResourceNotFoundException;
import org.film.parser.feature.tmdb.client.TMDBClient;
import org.film.parser.feature.tmdb.data.TMDBMovieDetails;
import org.film.parser.feature.torrent.client.JackettClient;
import org.film.parser.feature.torrent.data.JackettResult;
import org.film.parser.feature.torrent.data.Seed;
import org.film.parser.feature.torrent.data.TMBDMovieInfo;
import org.film.parser.feature.torrent.data.mappers.JackettResultToSeedMapper;
import org.film.parser.feature.torrent.repository.SeedCacheRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class TorrentServiceImpl implements TorrentService {

    private final TMDBClient tmdbClient;
    private final JackettClient jackettClient;
    private final TorrentQueryBuilder torrentQueryBuilder;
    private final SeedCacheRepository seedCacheRepository;
    private final JackettResultToSeedMapper seedMapper;
    private final RedisProperties redisProperties;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<Seed> searchSeeds(long tmdbId) {
        final var cached = seedCacheRepository.findByMovie(tmdbId);
        if (cached.isPresent()) {
            return seedMapper.fromJackett(cached.get());
        }

        final TMDBMovieDetails movie = tmdbClient.movieDetails(tmdbId, "ru-RU");
        final String year = movie.getReleaseDate() != null ? movie.getReleaseDate().substring(0, 4) : "";
        final TMBDMovieInfo movieInfo = TMBDMovieInfo.builder()
                .title(movie.getTitle())
                .year(year)
                .build();
        final List<JackettResult> jackettResults = jackettClient.search(torrentQueryBuilder.movieSearch(movieInfo));
        jackettResults.forEach(result -> {
            result.setTmdbId(tmdbId);
            result.setCacheGuid(cacheGuid(result));
        });

        final long ttl = jackettResults.isEmpty() ? redisProperties.negativeCacheSeconds() : redisProperties.torrentCachePerSeconds();
        seedCacheRepository.cacheByMovie(tmdbId, jackettResults, ttl);

        return seedMapper.fromJackett(jackettResults);
    }

    @Override
    public JackettResult getTorrent(long tmdbId, String guid) {
        return seedCacheRepository.findByMovie(tmdbId)
                .orElseThrow(() -> torrentNotFound(tmdbId, guid))
                .stream()
                .filter(torrent -> guid.equals(torrent.getCacheGuid()))
                .findFirst()
                .orElseThrow(() -> torrentNotFound(tmdbId, guid));
    }

    @Override
    public String requestDownload(long tmdbId, String guid) {
        final JackettResult torrentInfo = getTorrent(tmdbId, guid);
        try {
            final Map<String, Object> message = Map.of(
                    "type", "torrent",
                    "payload", torrentInfo);

            redisTemplate.opsForStream()
                    .add(redisProperties.downloadStream(), Map.of(redisProperties.streamMessageHeadKey(), objectMapper.writeValueAsString(message)));

            return torrentInfo.getCacheGuid();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send torrent to download queue for GUID: %s".formatted(guid), e);
        }
    }

    private String cacheGuid(JackettResult result) {
        return DigestUtils.sha256Hex(result.getGuid());
    }

    private ResourceNotFoundException torrentNotFound(long tmdbId, String guid) {
        return new ResourceNotFoundException("Torrent not found in cache (expired or invalid): tmdbId=%d, guid=%s".formatted(tmdbId, guid));
    }
}
