package org.film.parser.feature.torrent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.film.parser.core.configuration.properties.RedisProperties;
import org.film.parser.feature.torrent.data.JackettResult;
import org.film.parser.feature.torrent.data.Seed;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class RedisSeedsService implements LocalCacheSeedsService {

    final RedisProperties redisProperties;
    final StringRedisTemplate redisTemplate;
    final ObjectMapper objectMapper;

    @Override
    public List<Seed> sendTorrents(List<JackettResult> jackettResults) {
        ArrayList<Seed> seeds = new ArrayList<>();

        redisTemplate.executePipelined((RedisCallback<?>) callback -> {
            final StringRedisConnection connection = (StringRedisConnection) callback;
            for (final JackettResult jackettResult : jackettResults) {
                jackettResult.setCacheGuid(idBuilder(jackettResult));

                try {
                    String json = objectMapper.writeValueAsString(jackettResult);

                    connection.setEx(keyBuilder(jackettResult.getCacheGuid()), redisProperties.torrentCachePerSeconds(), json);

                    final Seed seed = Seed.builder()
                            .guid(jackettResult.getCacheGuid())
                            .title(jackettResult.getTitle())
                            .externalLink(jackettResult.getLink())
                            .build();

                    seeds.add(seed);
                } catch (JsonProcessingException e) {
                    log.error("[RedisSeedsService.sendTorrents] Error during mapping seeds to json", e);
                }
            }
            return null;
        });

        return seeds;
    }

    @Override
    public JackettResult getTorrent(String guid) {
        try {
            final String json = redisTemplate.opsForValue().get(keyBuilder(guid));
            if (json == null || json.isEmpty()) {
                throw new RuntimeException("Line not found ");
            }

            return objectMapper.readValue(json, JackettResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Miss cache for GUID: %s".formatted(guid), e);
        }
    }

    String keyBuilder(String guid) {
        return "seed:%s".formatted(guid);
    }

    String idBuilder(JackettResult seed) {
        return DigestUtils.sha256Hex(seed.getGuid());
    }

}
