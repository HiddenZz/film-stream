package org.film.parser.feature.playlist.service;

import lombok.AllArgsConstructor;
import org.film.parser.feature.playlist.client.ContentPlaylistFileStorageClient;
import org.film.parser.feature.playlist.client.MasterPlaylistFileStorageClient;
import org.film.parser.feature.playlist.data.*;
import org.film.parser.feature.playlist.repository.ContentHlsFetchedMetaRepository;
import org.film.parser.feature.playlist.repository.ContentHlsMetadataRepository;
import org.film.parser.feature.playlist.repository.MasterPlaylistMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
@AllArgsConstructor
public class SavePlaylistInfoServiceImpl implements SavePlaylistInfoService {

    private final MasterPlaylistMetadataRepository playlistMetadataRepository;
    private final MasterPlaylistFileStorageClient masterPlaylistFileStorageClient;


    private final ContentPlaylistFileStorageClient contentPlaylistFileStorageClient;
    private final ContentHlsMetadataRepository contentHlsMetadataRepository;

    private final ContentHlsFetchedMetaRepository contentHlsFetchedMetaRepository;


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
    public void saveContentPlaylistInfo(String path, ContentHlsFetchedMeta fetchedMeta,
                                        ContentPlaylistMedia contentPlaylistMedia) {
        final int insertedFetchedMeta = contentHlsFetchedMetaRepository.insert(
                fetchedMeta
        );

        contentPlaylistMedia.contentVariants().forEach(
                variant -> variant.setContentHlsFetchMetaId(insertedFetchedMeta));

        contentHlsMetadataRepository.saveAll(contentPlaylistMedia.contentVariants());

        contentPlaylistFileStorageClient.save(path, new ByteArrayInputStream(contentPlaylistMedia.content()));
    }
}
