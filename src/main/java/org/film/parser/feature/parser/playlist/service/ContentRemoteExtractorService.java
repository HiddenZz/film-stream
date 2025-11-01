package org.film.parser.feature.parser.playlist.service;

import org.film.parser.feature.parser.playlist.data.exceptions.ContentExtractException;
import org.film.parser.feature.parser.playlist.data.exceptions.ContentRemoteExtractException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ContentRemoteExtractorService implements ContentExtractorService {

    final RestClient restClient;

    ContentRemoteExtractorService(RestClient restClient) {
        this.restClient = restClient;
    }


    @Override
    public byte[] extract(String path) {

        try {
            final ResponseEntity<byte[]> response = restClient.get().uri(path).retrieve().toEntity(byte[].class);
            return response.getBody();

        } catch (final Exception e) {
            throw new ContentRemoteExtractException("Failed to extract remote content from path: " + path, e);
        }
    }
}
