package org.film.parser.feature.playlist.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.film.parser.feature.playlist.data.MasterPlaylistMetadata;
import org.film.parser.feature.playlist.data.StorageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Mapper
public interface MasterPlaylistMetadataMapper {

    int insert(MasterPlaylistMetadata metadata);

    int update(MasterPlaylistMetadata metadata);

    Optional<MasterPlaylistMetadata> findById(long id);

    Optional<MasterPlaylistMetadata> findByContentId(long contentId);

    List<MasterPlaylistMetadata> findByStatus(StorageStatus status);

    List<MasterPlaylistMetadata> findByStatusAndUpdatedAtBefore(StorageStatus status, LocalDateTime updatedBefore);

    List<MasterPlaylistMetadata> findStuckPending(LocalDateTime createdBefore);

    int updateStatus(@Param("id") long id, @Param("status") StorageStatus status);

    int updateStatusAndMinioKey(@Param("id") long id, @Param("minioKey") String minioKey,
                                @Param("status") StorageStatus status);

    int markAsFailed(@Param("id") long id, @Param("errorMessage") String errorMessage);

    int updateLastAccessed(long contentId);

    int deleteById(long id);

    int deleteByContentId(long contentId);

    int deleteOldRecords(LocalDateTime createdBefore);
}
