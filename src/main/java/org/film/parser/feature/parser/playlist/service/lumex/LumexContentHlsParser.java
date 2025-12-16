package org.film.parser.feature.parser.playlist.service.lumex;

import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.configuration.properties.external.LumexConfig;
import org.film.parser.feature.parser.playlist.data.ParsedContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.parser.playlist.data.exceptions.ContentParseException;
import org.film.parser.feature.parser.playlist.service.ContentPlaylistParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class LumexContentHlsParser implements ContentPlaylistParserService {
    final RestClient restClient;

    final String name;

    public LumexContentHlsParser(RestClient lumexRestClient, LumexConfig config) {
        this.restClient = lumexRestClient;
        this.name = config.name();
    }

    @Override
    public ParsedContentPlaylistMedia parse(String masterHlsUrl, String url) {
        try {
            final String normalizedUrl = normalizeUrl(masterHlsUrl, url);
            final FetchedData playlistData = fetchContentPlaylist(normalizedUrl);

            return ParsedContentPlaylistMedia.builder()
                                             .name("lumex")
                                             .hlsUrl(playlistData.fetchedUrl)
                                             .contentPlaylist(playlistData.data)
                                             .build();

        } catch (ContentParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed parsing lumex content playlist for masterHls: {} with url: {}", masterHlsUrl, url, e);
            throw new ContentParseException();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    FetchedData fetchContentPlaylist(String url) {
        final AtomicReference<URI> finalUri = new AtomicReference<>();

        final ResponseEntity<byte[]> response = restClient.get()
                                                          .uri(url)
                                                          .exchange(
                                                                  (req, res) -> {
                                                                      finalUri.set(req.getURI());
                                                                      return ResponseEntity.ok(res.bodyTo(byte[].class));
                                                                  }
                                                          );

        if (response == null || response.getStatusCode().isError()) {
            throw new ContentParseException();
        }

        return new FetchedData(response.getBody(), finalUri.get().toString());
    }


    String normalizeUrl(String masterUrl, String urlPart) {
        try {
            URI base = new URI(masterUrl);
            URI resolved = base.resolve(urlPart);
            return resolved.toString();
        } catch (Exception e) {
            log.error("Failed to normalize URL: {} with part: {}", masterUrl, urlPart, e);
            throw new ContentParseException();
        }
    }


    private record FetchedData(byte[] data, String fetchedUrl) {}
}
