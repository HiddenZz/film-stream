CREATE TABLE content_playlist_meta
(
    id                        BIGSERIAL PRIMARY KEY,
    content_id                BIGINT    NOT NULL,
    content_hls_fetch_meta_id BIGINT    NOT NULL REFERENCES content_hls_fetch_meta (id) ON DELETE CASCADE,
    segment_index             INT       NOT NULL,
    media_path                TEXT      NOT NULL,
    fallback_url              TEXT      NOT NULL,
    created_at                TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_segment_lookup ON content_playlist_meta (content_id, media_path, segment_index);
