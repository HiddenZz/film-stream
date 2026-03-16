CREATE TABLE movies (
    id             BIGSERIAL PRIMARY KEY,
    tmdb_id        BIGINT NOT NULL UNIQUE,
    title          TEXT   NOT NULL,
    original_title TEXT,
    overview       TEXT,
    poster_path    TEXT,
    release_date   TEXT,
    vote_average   DOUBLE PRECISION,
    runtime        INT,
    created_at     TIMESTAMP DEFAULT NOW()
);
