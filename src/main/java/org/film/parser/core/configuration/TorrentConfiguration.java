package org.film.parser.core.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.film.parser.core.configuration.properties.JackettProperties;
import org.film.parser.feature.torrent.client.JackettClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties({JackettProperties.class})
@Configuration
public class TorrentConfiguration {

    @Bean
    public JackettClient jackettClient(JackettProperties properties) {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
        );

        final RestClient restClient = RestClient.builder()
                .messageConverters(converters -> {
                    converters.addFirst(new MappingJackson2HttpMessageConverter(objectMapper));
                }).baseUrl(properties.url())
                .build();

        return new JackettClient(
                properties,
                restClient
        );
    }
}
