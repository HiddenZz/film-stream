package org.film.parser.feature.movie.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.configuration.properties.RedisProperties;
import org.film.parser.feature.movie.data.ContentReadyEvent;
import org.film.parser.feature.movie.service.MovieService;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ResultStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final MovieService movieService;
    private final ObjectMapper objectMapper;
    private final RedisProperties redisProperties;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            final String json = message.getValue().get(redisProperties.streamMessageHeadKey());
            final ContentReadyEvent event = objectMapper.readValue(json, ContentReadyEvent.class);
            movieService.saveReady(event);
        } catch (Exception e) {
            log.error("Failed to process result stream record: {}", message.getId(), e);
        }
    }
}