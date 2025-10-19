package org.film.parser.feature.parser.playlist.data;

import lombok.Builder;


@Builder
public record Media(String url, int maxQuality, String name) {
}
