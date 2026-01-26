package org.film.parser.feature.parser.playlist.service.veveo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.configuration.properties.external.VeveoConfig;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.parser.playlist.data.VeveoCatalogEpisodes;
import org.film.parser.feature.parser.playlist.data.exceptions.ParseMasterPlaylistException;
import org.film.parser.feature.parser.playlist.service.MasterPlaylistParser;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VeveoMasterHlsParser implements MasterPlaylistParser {

    private final VeveoConfig config;
    private final RestClient restClient;
    private final ObjectMapper mapper;

    public VeveoMasterHlsParser(VeveoConfig config, RestClient veveoRestClient, ObjectMapper mapper) {
        this.config = config;
        this.restClient = veveoRestClient;
        this.mapper = mapper;
    }

    @Override
    public ParsedMasterMedia parse(String iframe, long contentId) {

        final String contentIdParam = getContentIdFromIframe(iframe);

        if (contentIdParam == null) {
            log.warn("Content ID not found in iframe: {} queryParam: {}", iframe, config.queryParamForRequestContent());
            throw new ParseMasterPlaylistException("Content ID not found in iframe");
        }

        final String apiToken = extractApiToken(iframe);

        if (apiToken == null) {
            log.warn("API token not found in iframe: {} apiTokenHeaderName: {}", iframe, config.apiTokenHeaderName());
            throw new ParseMasterPlaylistException("API token not found");
        }

        final String masterHlsUrl = parseUrlMasterHls(contentIdParam, apiToken);

        final byte[] masterPlaylist = parseHls(masterHlsUrl, apiToken);

        return ParsedMasterMedia.builder()
                .masterPlaylist(masterPlaylist)
                .name(getName())
                .parsedUrl(masterHlsUrl)
                .build();
    }

    @Override
    public String getName() {
        return config.name();
    }


    String parseUrlMasterHls(String contentId, String apiToken) {
        try {
            final URI uri = UriComponentsBuilder.fromUriString(config.contentMapUrl())
                    .queryParam(config.queryParamForRequestContent(), contentId)
                    .build()
                    .toUri();

            final List<Map<String, Object>> response = restClient.get()
                    .uri(uri)
                    .header(config.apiTokenHeaderName(), apiToken)
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.isEmpty()) {
                throw new ParseMasterPlaylistException("Empty response while parsing Veveo HLS playlist");
            }

            final VeveoCatalogEpisodes firstItem = mapper.convertValue(response.getFirst(), VeveoCatalogEpisodes.class);

            if (firstItem.m3u8MasterFilePath() != null && firstItem.m3u8MasterFilePath().contains(".m3u8")) {
                return firstItem.m3u8MasterFilePath();
            }

            for (VeveoCatalogEpisodes.VeveoEpisodeVariant fileItem : firstItem.episodeVariants()
            ) {
                if (fileItem.filepath().contains(".m3u8")) {
                    return fileItem.filepath();
                }
            }

            throw new ParseMasterPlaylistException("No .m3u8 playlist URL found in Veveo content response");

        } catch (Exception e) {
            log.error("Failed to extract Veveo HLS playlist URL for contentId: {}", contentId, e);
            throw new ParseMasterPlaylistException("Failed to extract Veveo HLS playlist URL");
        }

    }

    byte[] parseHls(String url, String apiToken) {
        try {
            final URI uri = UriComponentsBuilder.fromUriString(url)
                    .build()
                    .toUri();

            return restClient.get()
                    .uri(uri)
                    .header(config.apiTokenHeaderName(), apiToken)
                    .retrieve()
                    .body(byte[].class);

        } catch (Exception e) {
            log.error("Failed to download Veveo HLS playlist for url: {}", url, e);
            throw new ParseMasterPlaylistException("Failed to download Veveo HLS playlist");
        }

    }

    String extractApiToken(String iframe) {
        try {
            final String html = restClient.get()
                    .uri(iframe)
                    .retrieve()
                    .body(String.class);
            if (html == null) {
                throw new RuntimeException("Empty HTML content for iframe: " + iframe);
            }

            final Pattern pattern = Pattern.compile("DLE-API-TOKEN['\"]:\\s*['\"]([^'\"]+)['\"]");
            final Matcher matcher = pattern.matcher(html);

            if (matcher.find()) {
                return matcher.group(1);
            }

        } catch (Exception e) {
            log.error("Failed to download iframe html from url: {}", iframe, e);
        }
        return null;
    }


    String getContentIdFromIframe(String iframe) {
        Map<String, String> queryParams = getQueryParams(iframe);
        return queryParams.get(config.queryParamMovieIdForParseName());
    }

    Map<String, String> getQueryParams(String uriString) {
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(uriString).build().getQueryParams();

        return params.toSingleValueMap();
    }
}
