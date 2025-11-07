package org.film.parser.feature.playlist.service;


import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.parser.playlist.data.ParsedContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.parser.playlist.service.PlaylistParserService;
import org.film.parser.feature.playlist.cache.EphemeralCache;
import org.film.parser.feature.playlist.client.ContentPlaylistFileStorageClient;
import org.film.parser.feature.playlist.client.ContentPlaylistFileStorageClientImpl;
import org.film.parser.feature.playlist.client.MasterPlaylistFileStorageClient;
import org.film.parser.feature.playlist.client.MasterPlaylistFileStorageClientImpl;
import org.film.parser.feature.playlist.data.*;
import org.film.parser.feature.playlist.repository.MasterPlaylistMetadataRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class PlaylistServiceIml implements PlaylistService {

    private final EphemeralCache<Long, MasterMedia> playlistCache;
    private final MasterPlaylistFileStorageClient masterPlaylistFileStorageClient;
    private final ContentPlaylistFileStorageClient contentPlaylistFileStorageClient;
    private final PlaylistParserService playlistParserService;
    private final PlaylistNormalizer playlistNormalizer;
    private final SavePlaylistInfoService savePlaylistInfoService;
    private final MasterPlaylistMetadataRepository masterPlaylistMetadataRepository;
    private final ContentPlaylistFileStorageClientImpl contentPlaylistFileStorageClientImpl;

    public PlaylistServiceIml(EphemeralCache<Long, MasterMedia> masterMediaCache,
                              MasterPlaylistFileStorageClientImpl minioClient,
                              ContentPlaylistFileStorageClient contentPlaylistFileStorageClient,
                              PlaylistParserService playlistParserService, PlaylistNormalizer playlistNormalizer,
                              SavePlaylistInfoService savePlaylistInfoService,
                              MasterPlaylistMetadataRepository masterPlaylistMetadataRepository,
                              ContentPlaylistFileStorageClientImpl contentPlaylistFileStorageClientImpl) {
        this.playlistCache = masterMediaCache;
        this.masterPlaylistFileStorageClient = minioClient;
        this.contentPlaylistFileStorageClient = contentPlaylistFileStorageClient;
        this.playlistParserService = playlistParserService;
        this.playlistNormalizer = playlistNormalizer;
        this.savePlaylistInfoService = savePlaylistInfoService;
        this.masterPlaylistMetadataRepository = masterPlaylistMetadataRepository;
        this.contentPlaylistFileStorageClientImpl = contentPlaylistFileStorageClientImpl;
    }


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
                    id -> preparationMasterMedia(playlistParserService.parseMasterPlaylist(id)),
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
    public Playlist getMediaPlaylist(long contentId, int quality) {
        final boolean isExist = contentPlaylistFileStorageClient.exists(contentId, quality);
        if (isExist) {
            try {
                return new Playlist(
                        new InputStreamResource(contentPlaylistFileStorageClientImpl.get(contentId, quality))
                );
            } catch (Exception e) {
                log.warn("Failed to load from storage for contentId: {}. Will parse from source.", contentId, e);
            }
        }

        try {
            final MasterPlaylistMetadata metadata = getMetadata(contentId);
            final ParsedContentPlaylistMedia parsedMedia = playlistParserService.parseContentPlaylist(contentId, metadata.getUrlByQuality(quality), metadata.parserServiceName());

            savePlaylistInfoService.saveContentPlaylistInfo(contentId, ContentPlaylistMedia.builder()
                                                                                           .content(parsedMedia.contentPlaylist())
                                                                                           .quality(quality)
                                                                                           .build());
        } catch (Exception e) {
            log.error("Failed to parse content playlist for contentId: {}, quality: {}", contentId, quality, e);
            throw new RuntimeException("Failed to get content playlist", e);
        }

        return null;
    }

    private MasterPlaylistMetadata getMetadata(long contentId) {
        return masterPlaylistMetadataRepository.findByContentId(contentId)
                                               .orElseThrow(() -> new RuntimeException("Metadata not found for contentId: " + contentId));
    }


    private MasterMedia preparationMasterMedia(ParsedMasterMedia parsedMasterMedia) {
        final MasterMediaNormalized normalized = playlistNormalizer.normalizeMasterPlaylist(parsedMasterMedia.masterPlaylist());

        return new MasterMedia(normalized.content(), normalized.mediaVariants(), parsedMasterMedia);
    }
}

