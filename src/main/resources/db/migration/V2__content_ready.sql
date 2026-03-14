CREATE TABLE content_ready
(
    id           BIGSERIAL PRIMARY KEY,
    tmdb_id      BIGINT      NOT NULL UNIQUE,
    content_uuid TEXT        NOT NULL,
    minio_path   TEXT        NOT NULL,
    created_at   TIMESTAMP DEFAULT NOW()
)
