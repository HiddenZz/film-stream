//package org.film.parser.feature.parser.playlist.service.lumex;
//
//import lombok.extern.slf4j.Slf4j;
//import org.film.parser.feature.configuration.properties.external.LumexConfig;
//import org.film.parser.feature.parser.playlist.data.ParsedContentPlaylistMedia;
//import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
//import org.film.parser.feature.parser.playlist.data.exceptions.ContentParseException;
//import org.film.parser.feature.parser.playlist.service.ContentPlaylistParserService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//
//@Slf4j
//public class LumexContentHlsParser implements ContentPlaylistParserService {
//    final RestClient restClient;
//
//    final String name;
//
//    public LumexContentHlsParser(RestClient lumexRestClient, LumexConfig config) {
//        this.restClient = lumexRestClient;
//        this.name = config.name();
//    }
//
//    @Override
//    public ParsedContentPlaylistMedia parse(ParsedMasterMedia parsedMasterMedia, String url) {
//        try {
//            final byte[] playlistData = fetchContentPlaylist(parsedMasterMedia, url);
//
//            String playlistContent = new String(playlistData, StandardCharsets.UTF_8);
//            log.debug("Playlist content: {}", playlistContent);
//
//            return ParsedContentPlaylistMedia.builder()
//                                             .name("lumex")
//                                             .contentPlaylist(playlistData)
//                                             .build();
//
//        } catch (ContentParseException e) {
//            throw e;
//        } catch (Exception e) {
//            log.error("Failed parsing lumex content playlist for media: {} with url: {}", parsedMasterMedia, url, e);
//            throw new ContentParseException();
//        }
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    byte[] fetchContentPlaylist(ParsedMasterMedia parsedMasterMedia, String urlPart) {
//
//        final String url = normalizeUrl(parsedMasterMedia.parsedUrl(), urlPart);
//
//        final ResponseEntity<byte[]> response = restClient.get()
//                                                          .uri(url)
//                                                          .retrieve()
//                                                          .toEntity(byte[].class);
//
//        if (response.getStatusCode().isError()) {
//            throw new ContentParseException();
//        }
//
//        return response.getBody();
//    }
//
//
//    String normalizeUrl(String masterUrl, String urlPart) {
//        try {
//            URI base = new URI(masterUrl);
//            URI resolved = base.resolve(urlPart);
//            return resolved.toString();
//        } catch (Exception e) {
//            log.error("Failed to normalize URL: {} with part: {}", masterUrl, urlPart, e);
//            throw new ContentParseException();
//        }
//    }
//}
