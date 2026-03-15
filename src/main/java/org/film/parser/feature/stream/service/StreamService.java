package org.film.parser.feature.stream.service;

import java.io.InputStream;

public interface StreamService {

    InputStream openMaster(String contentUuid);

    InputStream openPlaylist(String contentUuid, String quality);

    InputStream openSegment(String contentUuid, String quality, String segment);
}