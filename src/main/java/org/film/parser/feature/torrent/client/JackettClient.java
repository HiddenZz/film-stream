package org.film.parser.feature.torrent.client;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.configuration.properties.JackettProperties;
import org.film.parser.feature.torrent.data.JackettResult;
import org.film.parser.feature.torrent.data.exceptions.JackettException;
import org.film.parser.feature.torrent.data.JackettResults;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class JackettClient {

    private final JackettProperties properties;
    private final RestClient restClient;


    public List<JackettResult> search(String query) {
        final Map<String, String> params = new HashMap<>() {
            {
                put("apikey", properties.apiKey());
                put("Query", query);
            }
        };

        final JackettResults results = restClient.get().uri(uriBuilder -> {
            params.forEach(uriBuilder::queryParam);
            return uriBuilder.path("indexers/all/results/").build();
        }).retrieve().toEntity(JackettResults.class).getBody();

        if (results == null || results.getResults() == null) {
            throw new JackettException("Exception during search seeds");
        }

        return results.getResults();
    }
}
