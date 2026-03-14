CREATE TABLE content_seeds
(
    id           BIGSERIAL PRIMARY KEY,
    content_uuid TEXT UNIQUE NOT NULL,
    tmdb_id      BIGINT      NOT NULL,
    created_at   TIMESTAMP DEFAULT NOW()
)