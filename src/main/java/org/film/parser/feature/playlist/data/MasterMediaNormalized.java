package org.film.parser.feature.playlist.data;

import java.util.List;

public record MasterMediaNormalized(byte[] content, List<MediaVariant> mediaVariants) {
}
