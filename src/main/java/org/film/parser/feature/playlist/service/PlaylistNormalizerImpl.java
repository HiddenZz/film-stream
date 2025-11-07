package org.film.parser.feature.playlist.service;

import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.Resolution;
import io.lindstrom.m3u8.model.Variant;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.ParsingMode;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.playlist.data.MasterMedia;
import org.film.parser.feature.playlist.data.MasterMediaNormalized;
import org.film.parser.feature.playlist.data.MediaVariant;
import org.film.parser.feature.playlist.data.exceptions.PlaylistNormalizeContentException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class PlaylistNormalizerImpl implements PlaylistNormalizer {

    private final MasterPlaylistParser parser;

    public PlaylistNormalizerImpl() {
        this.parser = new MasterPlaylistParser(ParsingMode.LENIENT);
    }

    @Override
    public MasterMediaNormalized normalizeMasterPlaylist(byte[] media) {
        try {
            String content = new String(media, StandardCharsets.UTF_8);

            final MasterPlaylist playlist = parser.readPlaylist(content);


            final MasterPlaylist masterPlaylist = MasterPlaylist.builder()
                                                                .from(playlist)
                                                                .variants(normalizeVariants(playlist.variants()))
                                                                .build();

            String normalizedContent = parser.writePlaylistAsString(masterPlaylist);

            return new MasterMediaNormalized(normalizedContent.getBytes(StandardCharsets.UTF_8), playlist.variants()
                                                                                                         .stream()
                                                                                                         .flatMap(variant -> variant.resolution()
                                                                                                                                    .stream()
                                                                                                                                    .map(resolution -> new MediaVariant(resolution.height(), variant.uri())))
                                                                                                         .toList());

        } catch (Exception e) {
            log.error("Failed to normalize playlist Error: {}", e.getMessage(), e);
            throw new PlaylistNormalizeContentException("Failed to normalize playlist", e);
        }
    }

    private List<Variant> normalizeVariants(List<Variant> variants) {
        return variants.stream()
                       .map(this::normalizeVariant)
                       .distinct()
                       .sorted(Comparator.comparing(Variant::bandwidth).reversed())
                       .toList();
    }


    private Variant normalizeVariant(Variant variant) {
        final Optional<Resolution> resolution = variant.resolution();

        if (resolution.isEmpty()) {
            throw new PlaylistNormalizeContentException("Failed to normalize playlist. No resolution for:%s ".formatted(variant.uri()));
        }

        String normalizedUri = normalizeUri(String.valueOf(resolution.get().height()));

        return Variant.builder()
                      .from(variant)
                      .uri(normalizedUri)
                      .build();
    }


    private String normalizeUri(String resolution) {
        return "%s/index.m3u8".formatted(resolution);
    }
}

