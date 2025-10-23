package org.film.parser.feature.parser.playlist.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.util.SupplierWithException;
import org.film.parser.feature.parser.playlist.data.LumexContentPlayer;
import org.film.parser.feature.parser.playlist.data.LumexResponse;
import org.film.parser.feature.parser.playlist.data.Media;
import org.film.parser.feature.parser.playlist.data.exceptions.ParseIframeException;
import org.film.parser.feature.parser.playlist.data.exceptions.PlaylistDownloadException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
public class LumexMasterPlaylistParserService implements MasterPlaylistParserService {
    private final String host = "https://api.lumex.space";

    private final ObjectMapper mapper;
    private final MinioClient minioClient;
    private final RestClient lumexRestClient;

    public LumexMasterPlaylistParserService(ObjectMapper mapper, MinioClient minioClient, RestClient lumexRestClient) {
        this.mapper = mapper;
        this.minioClient = minioClient;
        this.lumexRestClient = lumexRestClient;
    }

    @Override
    public Media parse(String iframe, long contentId) {


        try {
            final LumexContentPlayer.LumexContentPlayerMedia lumexContent = extractFirstMedia(iframe);

            final String parsedUrl = parseUrlMasterPlaylist(lumexContent);
            final byte[] playlistData = downloadMasterPlaylist(parsedUrl);

            log.debug("Parsed url: {}", parsedUrl);
            String playlistContent = new String(playlistData, StandardCharsets.UTF_8);
            log.debug("Playlist content: {}", playlistContent);
        } catch (PlaylistDownloadException | ParseIframeException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed saving lumex media to minio", e);
        }

        return Media.builder().build();
    }


    private byte[] downloadMasterPlaylist(String playlistUrl) {
        try {


            final ResponseEntity<byte[]> response = lumexRestClient.get()
                                                                   .uri(new URI(playlistUrl))
                                                                   .retrieve()
                                                                   .toEntity(byte[].class);

            if (response.getStatusCode().isError()) {
                throw new PlaylistDownloadException("Failed to download master playlist: HTTP " + response.getStatusCode());
            }

            return response.getBody();
        } catch (PlaylistDownloadException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Error downloading master playlist from URL: {}", playlistUrl, e);
            throw new PlaylistDownloadException("Failed to download master playlist");
        }
    }

    private String parseUrlMasterPlaylist(LumexContentPlayer.LumexContentPlayerMedia media) {
        try {
            final String mediaUrl = media.playlist();


            final ResponseEntity<String> response = requestWithRetry(() -> lumexRestClient.post()
                                                                                          .uri(new URI(host + mediaUrl))
                                                                                          .retrieve()
                                                                                          .toEntity(String.class), 3);
            log.info("Body Parse Url master playlist response: {}", response.getBody());


            final Object mapped = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {})
                                        .getOrDefault("url", "");

            if (mapped instanceof String result && !result.isEmpty()) {
                return result;
            }

            throw new ParseIframeException("Invalid url type in lumex media: " + media);
        } catch (Exception e) {
            log.warn("Error when parsing url from lumex media: {}", media, e);
            throw new ParseIframeException();
        }
    }

    private <T> ResponseEntity<T> requestWithRetry(SupplierWithException<ResponseEntity<T>> request, int attempt) {
        if (attempt <= 0) {
            throw new RuntimeException("Attempts is less than 1 ");
        }

        try {
            final ResponseEntity<T> response = request.get();

            if (response == null || response.getStatusCode().isError()) {
                return requestWithRetry(request, attempt - 1);
            }

            return response;
        } catch (Exception e) {
            return requestWithRetry(request, attempt - 1);
        }

    }


    @NotNull
    private LumexContentPlayer.LumexContentPlayerMedia extractFirstMedia(String iframe) {
        try {
            final SegmentInfo segments = parseSegmentInfo(iframe);


            final ResponseEntity<String> response = lumexRestClient.get()
                                                                   .uri(buildUriForFetchMediaContent(segments))
                                                                   .retrieve()
                                                                   .toEntity(String.class);

            final LumexContentPlayer player = mapper.readValue(response.getBody(), LumexResponse.class).player();
            final LumexContentPlayer.LumexContentPlayerMedia lumexContent = player.media().stream().findFirst()
                                                                                  .orElse(null);

            if (lumexContent == null) {
                throw new ParseIframeException();
            }

            return lumexContent;

        } catch (Exception e) {
            log.warn("Error when parsing playlist iframe", e);
            throw new ParseIframeException();
        }
    }


    @NotNull
    private URI buildUriForFetchMediaContent(SegmentInfo segments) {

        return UriComponentsBuilder.fromUriString(host + "/content/")
                                   .queryParam("clientId", segments.clientId)
                                   .queryParam("contentType", segments.contentType)
                                   .queryParam("contentId", segments.contentId)
                                   .queryParam("domain", "reyohoho-gitlab.vercel.app")
                                   .queryParam("url", "reyohoho-gitlab.vercel.app")
                                   .build().toUri();
    }


    private SegmentInfo parseSegmentInfo(String iframe) {
        try {
            final URI uri = new URI("https:" + iframe);

            final String[] segments = Arrays.stream(uri.getPath().split("/"))
                                            .filter(s -> !s.isBlank())
                                            .toArray(String[]::new);

            if (segments.length < 1) {
                throw new RuntimeException();
            }

            final Function<Integer, String> getSegment = index -> {
                if (segments.length - 1 < index) {
                    return null;
                }

                return segments[index];
            };


            return new SegmentInfo(getSegment.apply(0), getSegment.apply(1), getSegment.apply(2));
        } catch (Exception e) {
            log.warn("Error when getting segments from iframe: {}", iframe, e);
            throw new ParseIframeException("Error when getting segments from iframe");
        }
    }


    @Data
    private static class SegmentInfo {
        final String clientId;
        final String contentType;
        final String contentId;
    }
}

