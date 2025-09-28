package org.film.parser.feature.configuration;

import org.film.parser.feature.configuration.properties.RestTemplateConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@EnableConfigurationProperties({RestTemplateConfigurationProperties.class})
@Configuration
public class RestTemplateConfiguration {

    @Bean
    RestTemplate restTemplate(RestTemplateConfigurationProperties props) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(props.getParseHost()));
        return restTemplate;
    }


}
