package org.film.parser.feature.playlist.service;


import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.parser.playlist.service.PlaylistParserService;
import org.film.parser.feature.playlist.cache.EphemeralCache;
import org.film.parser.feature.playlist.client.FileStorageClient;
import org.film.parser.feature.playlist.client.MinioClientImpl;
import org.film.parser.feature.playlist.data.MasterMedia;
import org.film.parser.feature.playlist.data.Playlist;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class PlaylistServiceIml implements PlaylistService {

    private final EphemeralCache<Long, MasterMedia> playlistCache;
    private final FileStorageClient fileStorageClient;
    private final PlaylistParserService playlistParserService;
    private final PlaylistNormalizer playlistNormalizer;
    private final SavePlaylistInfoService savePlaylistInfoService;


    public PlaylistServiceIml(EphemeralCache<Long, MasterMedia> masterMediaCache, MinioClientImpl minioClient,
                              PlaylistParserService playlistParserService, PlaylistNormalizer playlistNormalizer,
                              SavePlaylistInfoService savePlaylistInfoService) {
        this.playlistCache = masterMediaCache;
        this.fileStorageClient = minioClient;
        this.playlistParserService = playlistParserService;
        this.playlistNormalizer = playlistNormalizer;
        this.savePlaylistInfoService = savePlaylistInfoService;
    }


    @Override
    public Playlist getPlaylist(long contentId, String type) {
        final boolean isExist = fileStorageClient.masterPlaylistExist(String.valueOf(contentId));
        if (isExist) {
            try {
                log.debug("Loading playlist from storage for contentId: {}", contentId);
                return new Playlist(
                        new InputStreamResource(fileStorageClient.getMasterPlaylist(String.valueOf(contentId)))
                );
            } catch (Exception e) {
                log.warn("Failed to load from storage for contentId: {}. Will parse from source.", contentId, e);

            }
        }

        try {
            final EphemeralCache.CacheEntry<ParsedMasterMedia> entry = playlistCache.getOrCompute(contentId,
                    id -> {
                        ParsedMasterMedia parsed = playlistParserService.parseMasterPlaylist(id);

                        return new MasterMedia(playlistNormalizer.normalizeMasterPlaylist(parsed.masterPlaylist()), parsed);
                    },
                    value -> savePlaylistInfoService.saveMasterPlaylistInfo(contentId, value)
            ).get();


            return new Playlist(
                    new InputStreamResource(new ByteArrayInputStream(entry.value().masterPlaylist()))
            );

        } catch (InterruptedException e) {
            log.error("Interrupted while parsing playlist for contentId: {}", contentId, e);
            throw new RuntimeException("Failed to get playlist: interrupted", e);
        } catch (ExecutionException e) {
            log.error("Failed to parse playlist for contentId: {}", contentId, e.getCause());
            throw new RuntimeException("Failed to get playlist", e.getCause());
        }
    }
}
