package org.film.parser.feature.torrent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.film.parser.core.configuration.properties.RedisProperties;
import org.film.parser.feature.torrent.client.JackettClient;
import org.film.parser.feature.torrent.data.JackettResult;
import org.film.parser.feature.torrent.data.Seed;
import org.film.parser.feature.torrent.data.TMBDMovieInfo;
import org.film.parser.feature.torrent.data.mappers.JackettResultToSeedMapper;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class TorrentServiceImpl implements TorrentService {

    private final JackettClient jackettClient;
    private final TorrentQueryBuilder torrentQueryBuilder;
    private final LocalCacheSeedsService redisSeedsService;
    private final RedisProperties redisProperties;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<Seed> searchSeeds(long tmdbId) {
        //TODO: test data for download torrent. replace it with request to tmdb(:tmdbId)
        final TMBDMovieInfo movieInfo = TMBDMovieInfo.builder().year("2025").title("Чебурашка. Секрет праздника")
                .build();
        final List<JackettResult> jackettResults = jackettClient.search(torrentQueryBuilder.movieSearch(movieInfo));

        return redisSeedsService.sendTorrents(jackettResults);
    }

    @Override
    public JackettResult getTorrent(String guid) {
        return redisSeedsService.getTorrent(guid);
    }

    @Override
    public String requestDownload(String guid) {
        final JackettResult torrentInfo = redisSeedsService.getTorrent(guid);
        try {
            final Map<String, Object> message = Map.of(
                    "type", "torrent",
                    "payload", torrentInfo);

            redisTemplate.opsForStream()
                    .add(redisProperties.downloadStream(), Map.of(redisProperties.streamMessageHeadKey(), objectMapper.writeValueAsString(message)));

            return torrentInfo.getCacheGuid();
        } catch (Exception e) {
            throw new RuntimeException("Exception during content send to download queue");
        }

    }
}
