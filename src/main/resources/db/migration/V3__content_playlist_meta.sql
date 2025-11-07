CREATE TABLE content_playlist_meta
(
    id               BIGSERIAL PRIMARY KEY,
    playlist_id      INT            NOT NULL,
    quality          INT            NOT NULL,
    segment_index    INT            NOT NULL,
    segment_url      TEXT           NOT NULL,
    duration_seconds DECIMAL(10, 6) NOT NULL,
    offset_seconds   DECIMAL(10, 6) NOT NULL,
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE (playlist_id, quality, segment_index)
);

CREATE INDEX idx_segment_lookup ON content_playlist_meta (playlist_id, quality, segment_index);
