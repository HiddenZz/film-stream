CREATE TABLE master_playlist_metadata
(
    id                  BIGSERIAL PRIMARY KEY,
    content_id          BIGINT UNIQUE NOT NULL,
    parser_service_name VARCHAR(100)  NOT NULL,
    master_playlist_url TEXT          NOT NULL,
    minio_object_key    VARCHAR(500),
    status              VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    error_message       TEXT,
    created_at          TIMESTAMP              DEFAULT NOW(),
    updated_at          TIMESTAMP              DEFAULT NOW()
);

CREATE INDEX idx_status ON master_playlist_metadata (status);
CREATE INDEX idx_created_at ON master_playlist_metadata (created_at);