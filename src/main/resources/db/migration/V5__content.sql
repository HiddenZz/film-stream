CREATE TABLE content
(
    id           BIGSERIAL PRIMARY KEY,
    content_uuid UUID UNIQUE NOT NULL,
    tmdb_id      BIGINT      NOT NULL,
    quality      TEXT,
    minio_key    TEXT,
    state        TEXT        NOT NULL,
    progress     INT,
    error_cause  TEXT,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);