CREATE TABLE content_hls_fetch_meta
(
    id           BIGSERIAL PRIMARY KEY,
    content_id   BIGINT    NOT NULL,
    url          TEXT      NOT NULL,
    service_name TEXT      NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);
