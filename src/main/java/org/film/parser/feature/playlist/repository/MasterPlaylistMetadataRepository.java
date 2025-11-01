package org.film.parser.feature.playlist.repository;

import org.film.parser.feature.playlist.data.MasterPlaylistMetadata;
import org.film.parser.feature.playlist.data.StorageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface MasterPlaylistMetadataRepository {


    void save(MasterPlaylistMetadata metadata);

    void update(MasterPlaylistMetadata metadata);


    Optional<MasterPlaylistMetadata> findById(long id);


    Optional<MasterPlaylistMetadata> findByContentId(long contentId);


    List<MasterPlaylistMetadata> findByStatus(StorageStatus status);


    List<MasterPlaylistMetadata> findByStatusAndUpdatedAtBefore(StorageStatus status, LocalDateTime updatedBefore);


    List<MasterPlaylistMetadata> findStuckPending(LocalDateTime createdBefore);


    void updateStatus(long id, StorageStatus status);


    void updateStatusAndMinioKey(long id, String minioKey, StorageStatus status);

    void markAsFailed(long id, String errorMessage);


    void updateLastAccessed(long contentId);

    void deleteById(long id);

    void deleteByContentId(long contentId);


    int deleteOldRecords(LocalDateTime createdBefore);


    boolean existsByContentId(long contentId);
}
