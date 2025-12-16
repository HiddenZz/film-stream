package org.film.parser.feature.playlist.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.parser.playlist.data.ParsedContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.parser.playlist.service.ContentExtractorService;
import org.film.parser.feature.parser.playlist.service.PlaylistParserService;
import org.film.parser.feature.playlist.cache.EphemeralCache;
import org.film.parser.feature.playlist.client.ContentPlaylistFileStorageClient;
import org.film.parser.feature.playlist.client.ContentPlaylistFileStorageClientImpl;
import org.film.parser.feature.playlist.client.MasterPlaylistFileStorageClient;
import org.film.parser.feature.playlist.client.MasterPlaylistFileStorageClientImpl;
import org.film.parser.feature.playlist.data.*;
import org.film.parser.feature.playlist.repository.ContentHlsFetchedMetaRepository;
import org.film.parser.feature.playlist.repository.ContentHlsMetadataRepository;
import org.film.parser.feature.playlist.repository.MasterPlaylistMetadataRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@AllArgsConstructor
public class PlaylistServiceIml implements PlaylistService {

    private final EphemeralCache<Long, MasterMedia> playlistCache;
    private final MasterPlaylistFileStorageClient masterPlaylistFileStorageClient;
    private final ContentPlaylistFileStorageClient contentPlaylistFileStorageClient;
    private final PlaylistParserService playlistParserService;
    private final MasterHlsNormalizer masterHlsNormalizer;
    private final ContentHlsNormalizer contentHlsNormalizer;
    private final SavePlaylistInfoService savePlaylistInfoService;
    private final MasterPlaylistMetadataRepository masterPlaylistMetadataRepository;
    private final ContentPlaylistFileStorageClientImpl contentPlaylistFileStorageClientImpl;
    private final ContentHlsMetadataRepository contentHlsMetadataRepository;
    private final ContentExtractorService contentExtractorService;
    private final ContentHlsFetchedMetaRepository contentHlsFetchedMetaRepository;


    @Override
    public Playlist getMasterPlaylist(long contentId, String type) {
        final boolean isExist = masterPlaylistFileStorageClient.masterPlaylistExist(String.valueOf(contentId));
        if (isExist) {
            try {
                return new Playlist(
                        new InputStreamResource(masterPlaylistFileStorageClient.getMasterPlaylist(String.valueOf(contentId)))
                );
            } catch (Exception e) {
                log.warn("Failed to load from storage for contentId: {}. Will parse from source.", contentId, e);
            }
        }

        try {
            final EphemeralCache.CacheEntry<MasterMedia> entry = playlistCache.getOrCompute(contentId,
                    id -> preparationMasterMedia(id, playlistParserService.parseMasterPlaylist(id)),
                    value -> savePlaylistInfoService.saveMasterPlaylistInfo(contentId, value)
            ).get();

            return new Playlist(
                    new InputStreamResource(new ByteArrayInputStream(entry.value().content()))
            );

        } catch (InterruptedException e) {
            log.error("Interrupted while parsing playlist for contentId: {}", contentId, e);
            throw new RuntimeException("Failed to get playlist: interrupted", e);
        } catch (ExecutionException e) {
            log.error("Failed to parse playlist for contentId: {}", contentId, e.getCause());
            throw new RuntimeException("Failed to get playlist", e.getCause());
        }
    }


    @Override
    public Playlist getMediaPlaylistByPath(String fullPath, long contentId) {
        final boolean isExist = contentPlaylistFileStorageClient.exists(fullPath);
        if (isExist) {
            try {
                return new Playlist(
                        new InputStreamResource(contentPlaylistFileStorageClientImpl.get(fullPath))
                );
            } catch (Exception e) {
                log.warn("Failed to load from storage for path: {}. Will parse from source.", fullPath, e);
            }
        }
        try {
            final MasterPlaylistMetadata metadata = masterPlaylistMetadataRepository.findByContentId(contentId)
                                                                                    .orElseThrow(() -> new RuntimeException("Metadata not found for contentId: " + contentId));

            final MediaVariant variant = metadata.getVariantByNormalizedPath(fullPath);

            final ParsedContentPlaylistMedia parsedMedia = playlistParserService.parseContentPlaylist(contentId, metadata.masterPlaylistUrl(), variant.fallbackUrl(), metadata.parserServiceName());

            final ContentPlaylistMedia normalizedContentPlaylist = contentHlsNormalizer.normalize(
                    parsedMedia.contentPlaylist(),
                    variant,
                    contentId
            );

            final ContentHlsFetchedMeta fetchedMeta = ContentHlsFetchedMeta.builder()
                                                                           .url(parsedMedia.hlsUrl())
                                                                           .serviceName(parsedMedia.name())
                                                                           .contentId(contentId)
                                                                           .build();

            savePlaylistInfoService.saveContentPlaylistInfo(fullPath, fetchedMeta, normalizedContentPlaylist);


            return new Playlist(
                    new InputStreamResource(new ByteArrayInputStream(normalizedContentPlaylist.content())
                    ));
        } catch (Exception e) {
            log.error("Failed to parse content playlist for contentId: {}, quality: {}", contentId, fullPath, e);
            throw new RuntimeException("Failed to get content playlist", e);
        }

    }

    @Override
    public Playlist getMovie(String fullPath, long contentId, int index) {
        if (contentPlaylistFileStorageClient.exists(fullPath)) {
            try {
                return new Playlist(
                        new InputStreamResource(contentPlaylistFileStorageClientImpl.get(fullPath))
                );
            } catch (Exception e) {
                log.warn("Failed to load movie from storage for path: {}.", fullPath, e);
            }
        }

        try {
            final ContentMediaMeta mediaMeta = contentHlsMetadataRepository.findByInfo(contentId, index, fullPath);
            String url = mediaMeta.getFallbackUrl();
            if (!UriComponentsBuilder.fromUriString(url).build().toUri().isAbsolute()) {
                final ContentHlsFetchedMeta meta = contentHlsFetchedMetaRepository.findById(mediaMeta.getContentHlsFetchMetaId());
                url = UriComponentsBuilder.fromUriString(meta.url())
                                          .build().toUri().resolve(url).toString();

            }

            final byte[] movieData = contentExtractorService.extract(url);

            return new Playlist(new InputStreamResource(new ByteArrayInputStream(movieData)));
        } catch (Exception e) {
            log.error("Failed to get movie for contentId: {}, path: {}", contentId, fullPath, e);
            throw new RuntimeException("Failed to get movie", e);
        }

    }


    private MasterMedia preparationMasterMedia(long contentId, ParsedMasterMedia parsedMasterMedia) {
        final MasterMediaNormalized normalized = masterHlsNormalizer.normalize(
                parsedMasterMedia.masterPlaylist(),
                contentId
        );

        return new MasterMedia(normalized.content(), normalized.mediaVariants(), parsedMasterMedia);
    }
}

