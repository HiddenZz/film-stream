ALTER TABLE content_ready DROP CONSTRAINT content_ready_tmdb_id_key;
ALTER TABLE content_ready ADD CONSTRAINT content_ready_content_uuid_key UNIQUE (content_uuid);
