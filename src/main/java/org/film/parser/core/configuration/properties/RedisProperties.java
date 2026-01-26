package org.film.parser.core.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

@ConfigurationProperties("storage.redis")
public record RedisProperties(String downloadStream, String streamMessageHeadKey, long torrentCachePerSeconds) {

}
