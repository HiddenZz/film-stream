package org.film.parser.feature.playlist.repository;

import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.playlist.data.MasterPlaylistMetadata;
import org.film.parser.feature.playlist.data.StorageStatus;
import org.film.parser.feature.playlist.repository.mapper.MasterPlaylistMetadataMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
public class MasterPlaylistMetadataRepositoryImpl implements MasterPlaylistMetadataRepository {

    private final MasterPlaylistMetadataMapper mapper;

    MasterPlaylistMetadataRepositoryImpl(MasterPlaylistMetadataMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(MasterPlaylistMetadata metadata) {
        log.debug("Saving playlist metadata: contentId={}, status={}",
                metadata.contentId(), metadata.status());

        int rows = mapper.insert(metadata);

        if (rows == 0) {
            throw new IllegalStateException("Failed to insert playlist metadata for contentId: " + metadata.contentId());
        }

        log.debug("Playlist metadata saved: id={}, contentId={}",
                metadata.id(), metadata.contentId());
    }

    @Override
    public void update(MasterPlaylistMetadata metadata) {
        log.debug("Updating playlist metadata: id={}, contentId={}, status={}",
                metadata.id(), metadata.contentId(), metadata.status());

        int rows = mapper.update(metadata);

        if (rows == 0) {
            throw new IllegalStateException("Failed to update playlist metadata for id: " + metadata.id());
        }

        log.debug("Playlist metadata updated: id={}, contentId={}",
                metadata.id(), metadata.contentId());
    }

    @Override
    public Optional<MasterPlaylistMetadata> findById(long id) {
        log.debug("Finding playlist metadata by id: {}", id);
        return mapper.findById(id);
    }

    @Override
    public Optional<MasterPlaylistMetadata> findByContentId(long contentId) {
        log.debug("Finding playlist metadata by contentId: {}", contentId);
        return mapper.findByContentId(contentId);
    }

    @Override
    public List<MasterPlaylistMetadata> findByStatus(StorageStatus status) {
        log.debug("Finding playlist metadata by status: {}", status);
        return mapper.findByStatus(status);
    }

    @Override
    public List<MasterPlaylistMetadata> findByStatusAndUpdatedAtBefore(StorageStatus status,
                                                                       LocalDateTime updatedBefore) {
        log.debug("Finding playlist metadata by status={} and updatedBefore={}", status, updatedBefore);
        return mapper.findByStatusAndUpdatedAtBefore(status, updatedBefore);
    }

    @Override
    public List<MasterPlaylistMetadata> findStuckPending(LocalDateTime createdBefore) {
        log.debug("Finding stuck pending records created before: {}", createdBefore);
        return mapper.findStuckPending(createdBefore);
    }

    @Override
    public void updateStatus(long id, StorageStatus status) {
        log.debug("Updating status for id={}: status={}", id, status);

        int rows = mapper.updateStatus(id, status);

        if (rows == 0) {
            log.warn("No rows updated for id={}, status={}", id, status);
        }
    }

    @Override
    public void updateStatusAndMinioKey(long id, String minioKey, StorageStatus status) {
        log.debug("Updating status and minioKey for id={}: status={}, minioKey={}",
                id, status, minioKey);

        int rows = mapper.updateStatusAndMinioKey(id, minioKey, status);

        if (rows == 0) {
            log.warn("No rows updated for id={}, status={}, minioKey={}", id, status, minioKey);
        }
    }

    @Override
    public void markAsFailed(long id, String errorMessage) {
        log.error("Marking as failed for id={}: error={}", id, errorMessage);

        int rows = mapper.markAsFailed(id, errorMessage);

        if (rows == 0) {
            log.warn("No rows updated when marking as failed for id={}", id);
        }
    }

    @Override
    public void updateLastAccessed(long contentId) {
        log.debug("Updating last accessed time for contentId: {}", contentId);
        mapper.updateLastAccessed(contentId);
    }

    @Override
    public void deleteById(long id) {
        log.debug("Deleting playlist metadata by id: {}", id);

        int rows = mapper.deleteById(id);

        if (rows == 0) {
            log.warn("No rows deleted for id={}", id);
        }
    }

    @Override
    public void deleteByContentId(long contentId) {
        log.debug("Deleting playlist metadata by contentId: {}", contentId);

        int rows = mapper.deleteByContentId(contentId);

        if (rows == 0) {
            log.warn("No rows deleted for contentId={}", contentId);
        }
    }

    @Override
    public int deleteOldRecords(LocalDateTime createdBefore) {
        log.info("Deleting old records created before: {}", createdBefore);

        int deletedCount = mapper.deleteOldRecords(createdBefore);

        log.info("Deleted {} old records", deletedCount);

        return deletedCount;
    }

    @Override
    public boolean existsByContentId(long contentId) {
        log.debug("Checking existence by contentId: {}", contentId);
        final var existing = mapper.findByContentId(contentId);
        return existing.isPresent();
    }
}
