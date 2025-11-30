package org.film.parser.feature.configuration.properties.external;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;


@ConfigurationProperties("parsers.movie.veveo")
@ConfigurationPropertiesBinding
public record VeveoConfig(String queryParamMovieIdForParseName, String apiTokenHeaderName,
                          String queryParamForRequestContent, String contentMapUrl, String name) {
}
