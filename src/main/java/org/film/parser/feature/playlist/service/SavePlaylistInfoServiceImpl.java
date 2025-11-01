package org.film.parser.feature.playlist.service;

import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.film.parser.feature.playlist.client.FileStorageClient;
import org.film.parser.feature.playlist.data.MasterMedia;
import org.film.parser.feature.playlist.data.MasterPlaylistMetadata;
import org.film.parser.feature.playlist.data.StorageStatus;
import org.film.parser.feature.playlist.repository.MasterPlaylistMetadataRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
public class SavePlaylistInfoServiceImpl implements SavePlaylistInfoService {

    private final MasterPlaylistMetadataRepository playlistMetadataRepository;
    private final FileStorageClient fileStorageClient;

    public SavePlaylistInfoServiceImpl(MasterPlaylistMetadataRepository playlistMetadataRepository,
                                       ApplicationEventPublisher eventPublisher, FileStorageClient fileStorageClient) {
        this.playlistMetadataRepository = playlistMetadataRepository;
        this.fileStorageClient = fileStorageClient;
    }


    @Override
    @Transactional
    public void saveMasterPlaylistInfo(long contentId, MasterMedia parsedMasterMedia) {
        final ParsedMasterMedia parsedMasterMediaData = parsedMasterMedia.parsedMasterMedia();

        MasterPlaylistMetadata metadata = MasterPlaylistMetadata.builder()
                                                                .contentId(contentId)
                                                                .parserServiceName(parsedMasterMedia.parsedMasterMedia()
                                                                                                    .name())
                                                                .masterPlaylistUrl(parsedMasterMedia.parsedMasterMedia()
                                                                                                    .parsedUrl())
                                                                .minioObjectKey(fileStorageClient.generateMasterKey(String.valueOf(contentId)))
                                                                .status(StorageStatus.PENDING).build();

        if (playlistMetadataRepository.existsByContentId(contentId)) {
            playlistMetadataRepository.update(metadata);
        } else {
            playlistMetadataRepository.save(metadata);
        }

        fileStorageClient.saveMasterPlaylist(String.valueOf(contentId), new ByteArrayInputStream(parsedMasterMedia.content()));
        fileStorageClient.saveMasterPlaylist(String.valueOf(contentId), "parsed-index.m3u8", new ByteArrayInputStream(parsedMasterMedia.parsedMasterMedia()
                                                                                                                                       .masterPlaylist()));
    }

}
