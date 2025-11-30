package org.film.parser.feature.playlist.service;

import org.film.parser.feature.playlist.client.ContentPlaylistFileStorageClient;
import org.film.parser.feature.playlist.client.MasterPlaylistFileStorageClient;
import org.film.parser.feature.playlist.data.ContentPlaylistMedia;
import org.film.parser.feature.playlist.data.MasterMedia;
import org.film.parser.feature.playlist.data.MasterPlaylistMetadata;
import org.film.parser.feature.playlist.data.StorageStatus;
import org.film.parser.feature.playlist.repository.ContentHlsMetadataRepository;
import org.film.parser.feature.playlist.repository.ContentHlsMetadataRepositoryImpl;
import org.film.parser.feature.playlist.repository.MasterPlaylistMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
public class SavePlaylistInfoServiceImpl implements SavePlaylistInfoService {

    private final MasterPlaylistMetadataRepository playlistMetadataRepository;
    private final MasterPlaylistFileStorageClient masterPlaylistFileStorageClient;


    private final ContentPlaylistFileStorageClient contentPlaylistFileStorageClient;
    private final ContentHlsMetadataRepository contentHlsMetadataRepository;

    public SavePlaylistInfoServiceImpl(MasterPlaylistMetadataRepository playlistMetadataRepository,
                                       MasterPlaylistFileStorageClient masterPlaylistFileStorageClient,
                                       ContentPlaylistFileStorageClient contentPlaylistFileStorageClient,
                                       ContentHlsMetadataRepository contentHlsMetadataRepository
    ) {
        this.playlistMetadataRepository = playlistMetadataRepository;
        this.masterPlaylistFileStorageClient = masterPlaylistFileStorageClient;
        this.contentPlaylistFileStorageClient = contentPlaylistFileStorageClient;
        this.contentHlsMetadataRepository = contentHlsMetadataRepository;
    }


    @Override
    @Transactional
    public void saveMasterPlaylistInfo(long contentId, MasterMedia parsedMasterMedia) {

        MasterPlaylistMetadata metadata = MasterPlaylistMetadata.builder()
                                                                .contentId(contentId)
                                                                .parserServiceName(parsedMasterMedia.parsedMasterMedia()
                                                                                                    .name())
                                                                .masterPlaylistUrl(parsedMasterMedia.parsedMasterMedia()
                                                                                                    .parsedUrl())
                                                                .minioObjectKey(masterPlaylistFileStorageClient.generateMasterKey(String.valueOf(contentId)))
                                                                .parsedVariants(parsedMasterMedia.mediaVariants())
                                                                .status(StorageStatus.PENDING)
                                                                .build();

        if (playlistMetadataRepository.existsByContentId(contentId)) {
            playlistMetadataRepository.update(metadata);
        } else {
            playlistMetadataRepository.save(metadata);
        }

        masterPlaylistFileStorageClient.saveMasterPlaylist(String.valueOf(contentId), new ByteArrayInputStream(parsedMasterMedia.content()));
    }

    @Override
    @Transactional
    public void saveContentPlaylistInfo(String path, ContentPlaylistMedia contentPlaylistMedia) {
        contentHlsMetadataRepository.saveAll(contentPlaylistMedia.contentVariants());

        contentPlaylistFileStorageClient.save(path, new ByteArrayInputStream(contentPlaylistMedia.content()));
        
    }
}
