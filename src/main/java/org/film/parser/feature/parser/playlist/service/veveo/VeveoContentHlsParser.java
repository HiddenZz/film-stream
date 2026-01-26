package org.film.parser.feature.parser.playlist.service.veveo;

import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.configuration.properties.external.VeveoConfig;
import org.film.parser.feature.parser.playlist.data.ParsedContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.exceptions.ContentParseException;
import org.film.parser.feature.parser.playlist.service.ContentPlaylistParserService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Slf4j
@Service
public class VeveoContentHlsParser implements ContentPlaylistParserService {

    private final VeveoConfig config;
    private final RestClient restClient;

    public VeveoContentHlsParser(VeveoConfig config, RestClient restClient) {
        this.config = config;
        this.restClient = restClient;
    }

    @Override
    public ParsedContentPlaylistMedia parse(String masterHlsUrl, String url) {
        final URI baseUrl = uriBuilder(masterHlsUrl, url);

        try {
            final byte[] playlistData = restClient.get()
                    .uri(baseUrl)
                    .retrieve()
                    .toEntity(byte[].class)
                    .getBody();

            return ParsedContentPlaylistMedia.builder().contentPlaylist(playlistData).name(getName()).build();

        } catch (final Exception e) {
            log.error("Failed parsing veveo content playlist for media: {} with url: {}", masterHlsUrl, url, e);
            throw new ContentParseException();
        }


    }

    @Override
    public String getName() {
        return config.name();
    }


    URI uriBuilder(String parsedMasterHlsUrl, String contentPlaylistUrl) {
        return UriComponentsBuilder.fromUriString(parsedMasterHlsUrl)
                .replacePath(contentPlaylistUrl)
                .build()
                .toUri();
    }
}
