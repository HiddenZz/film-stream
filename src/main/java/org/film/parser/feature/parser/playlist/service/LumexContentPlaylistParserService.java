package org.film.parser.feature.parser.playlist.service;

import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.parser.playlist.data.ContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.parser.playlist.data.exceptions.ContentParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@Service
public class LumexContentPlaylistParserService implements ContentPlaylistParserService {
    final RestClient restClient;

    public LumexContentPlaylistParserService(RestClient lumexRestClient) {
        this.restClient = lumexRestClient;
    }

    @Override
    public ContentPlaylistMedia parse(ParsedMasterMedia parsedMasterMedia, String resolution) {
        try {
            final byte[] playlistData = fetchContentPlaylist(parsedMasterMedia, resolution);

            String playlistContent = new String(playlistData, StandardCharsets.UTF_8);
            log.debug("Playlist content: {}", playlistContent);

            return ContentPlaylistMedia.builder()
                                       .name("lumex")
                                       .contentPlaylist(playlistData)
                                       .parsedUrl(parsedMasterMedia.parsedUrl())
                                       .resolution(resolution)
                                       .build();

        } catch (ContentParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed parsing lumex content playlist for media: {} with resolution: {}", parsedMasterMedia, resolution, e);
            throw new ContentParseException();
        }
    }

    byte[] fetchContentPlaylist(ParsedMasterMedia parsedMasterMedia, String resolution) {

        final String url = normalizeUrl(parsedMasterMedia.parsedUrl(), resolution);

        final ResponseEntity<byte[]> response = restClient.get()
                                                          .uri(url)
                                                          .retrieve()
                                                          .toEntity(byte[].class);

        if (response.getStatusCode().isError()) {
            throw new ContentParseException();
        }

        return response.getBody();
    }


    String normalizeUrl(String masterUrl, String resolution) {

        final ArrayList<String> parts = new ArrayList<>(Arrays.asList(masterUrl.split("/")));

        if (parts.getLast().contains(".m3u8")) {
            final String lastPart = String.format("%s/%s", resolution, parts.removeLast());

            return String.format("%s/%s", String.join("/", parts), lastPart);
        }

        return "";
    }
}
