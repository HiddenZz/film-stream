package org.film.parser.core.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage.redis")
public record RedisProperties(String downloadStream, String streamMessageHeadKey, long torrentCachePerSeconds) {

}
