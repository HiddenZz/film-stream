package org.film.parser.feature.playlist.data;

import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;

import java.util.List;

public record MasterMedia(byte[] content, List<MediaVariant> mediaVariants, ParsedMasterMedia parsedMasterMedia) {
}
