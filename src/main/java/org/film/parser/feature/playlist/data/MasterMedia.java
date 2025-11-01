package org.film.parser.feature.playlist.data;

import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;

public record MasterMedia(byte[] content, ParsedMasterMedia parsedMasterMedia) {
}
