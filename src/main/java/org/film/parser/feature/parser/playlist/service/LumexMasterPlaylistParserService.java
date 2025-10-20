package org.film.parser.feature.parser.playlist.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.util.SupplierWithException;
import org.film.parser.feature.parser.playlist.data.LumexContentPlayer;
import org.film.parser.feature.parser.playlist.data.LumexResponse;
import org.film.parser.feature.parser.playlist.data.Media;
import org.film.parser.feature.parser.playlist.data.exceptions.ParseIframeException;
import org.film.parser.feature.parser.playlist.data.exceptions.PlaylistDownloadException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Service
public class LumexMasterPlaylistParserService implements MasterPlaylistParserService {
    private final String host = "https://api.lumex.space";

    private final ObjectMapper mapper;
    private final MinioClient minioClient;
    private final HttpClient httpClient;

    public LumexMasterPlaylistParserService(ObjectMapper mapper, MinioClient minioClient) {
        this.mapper = mapper;
        this.minioClient = minioClient;
        this.httpClient = HttpClient.newBuilder() .followRedirects(HttpClient.Redirect.NORMAL)
                                    .cookieHandler(new CookieManager())
                                    .build();
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
            final HttpRequest request = baseHttpRequestBuilder(new URI(playlistUrl))
                    .GET()
                    .build();

            final HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 400) {
                throw new PlaylistDownloadException("Failed to download master playlist: HTTP " + response.statusCode());
            }

            return response.body();
        } catch (PlaylistDownloadException e){
            throw e;
        } catch (Exception e) {
            log.warn("Error downloading master playlist from URL: {}", playlistUrl, e);
            throw new PlaylistDownloadException("Failed to download master playlist");
        }
    }

    private String parseUrlMasterPlaylist(LumexContentPlayer.LumexContentPlayerMedia media) {
        try {
            final String mediaUrl = media.playlist();

            final HttpRequest request = baseHttpRequestBuilder(new URI(host + mediaUrl)).POST(HttpRequest.BodyPublishers.noBody())
                                                                                           .build();

            final HttpResponse<String> response =requestWithRetry(() -> httpClient.send(request, HttpResponse.BodyHandlers.ofString()), 3);
            log.info("Body Parse Url master playlist response: {}", response.body());


            final Object mapped = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {})
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

    private <T> HttpResponse<T> requestWithRetry(SupplierWithException<HttpResponse<T>> request, int attempt) {
       if(attempt <= 0){
           throw new RuntimeException("Attempts is less than 1 ");
       }

      try {
          final HttpResponse<T> response =  request.get();

          if(response == null || response.statusCode() >= 400){
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


            final HttpRequest request = baseHttpRequestBuilder(buildUriForFetchMediaContent(segments)).GET()
                                                                                                                                  .build();

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            final LumexContentPlayer player = mapper.readValue(response.body(), LumexResponse.class).player();
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
    private HttpRequest.Builder baseHttpRequestBuilder(URI uri) {
        return HttpRequest.newBuilder()
                          .uri(uri)
                          .header("Origin", "https://p.lumex.space")
                          .header("Referer", "https://p.lumex.space/")
                          .header("Sec-Fetch-Dest", "empty")
                          .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36");
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

            final String[] segments = Arrays.stream(uri.getPath().split("/")).filter(s -> !s.isBlank()).toArray(String[]::new);

            if (segments.length < 1) {
                throw new RuntimeException();
            }

            final Function<Integer, String> getSegment =  index -> {
                if(segments.length-1 < index){
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
    private static class SegmentInfo{
        final String clientId;
        final String contentType;
        final String contentId;
    }
}

