package org.film.parser.feature.configuration;

import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.playlist.cache.EphemeralCache;
import org.film.parser.feature.playlist.data.MasterMedia;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class CacheConfiguration {


    @Bean
    EphemeralCache<Long, MasterMedia> masterMediaCache() {
        final Executor executor = Executors.newFixedThreadPool(4, r -> {
            Thread thread = new Thread(r);
            thread.setName("thread-master-media-ephemeral-cache");
            thread.setDaemon(true);
            return thread;
        });

        return new EphemeralCache<>(executor, "master-media-ephemeral-cache");
    }
}
