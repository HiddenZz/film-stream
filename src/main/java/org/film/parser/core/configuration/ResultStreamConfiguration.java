package org.film.parser.core.configuration;

import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.configuration.properties.RedisProperties;
import org.film.parser.feature.movie.consumer.ResultStreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@Slf4j
@Configuration
public class ResultStreamConfiguration {

    private static final String GROUP = "film-stream-group";
    private static final String CONSUMER = "film-stream-consumer";

    @Bean
    StreamMessageListenerContainer<String, MapRecord<String, String, String>> resultStreamListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisProperties redisProperties,
            StringRedisTemplate redisTemplate,
            ResultStreamListener listener) {

        initConsumerGroup(redisTemplate, redisProperties.resultStream());

        final StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .build();

        final StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        container.receive(
                Consumer.from(GROUP, CONSUMER),
                StreamOffset.create(redisProperties.resultStream(), ReadOffset.lastConsumed()),
                listener
        );

        container.start();
        return container;
    }

    private void initConsumerGroup(StringRedisTemplate redisTemplate, String stream) {
        try {
            redisTemplate.opsForStream().createGroup(stream, ReadOffset.from("0"), GROUP);
            log.info("Consumer group '{}' created for stream '{}'", GROUP, stream);
        } catch (Exception e) {
            log.debug("Consumer group '{}' already exists for stream '{}'", GROUP, stream);
        }
    }
}