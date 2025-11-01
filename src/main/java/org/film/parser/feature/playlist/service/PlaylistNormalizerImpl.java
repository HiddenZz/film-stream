package org.film.parser.feature.playlist.service;

import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.Resolution;
import io.lindstrom.m3u8.model.Variant;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.ParsingMode;
import lombok.extern.slf4j.Slf4j;
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
    public byte[] normalizeMasterPlaylist(byte[] media) {
        try {
            String playlistContent = new String(media, StandardCharsets.UTF_8);

            MasterPlaylist masterPlaylist = parser.readPlaylist(playlistContent);

            MasterPlaylist normalizedPlaylist = normalizeMasterPlaylist(masterPlaylist);

            String normalizedContent = parser.writePlaylistAsString(normalizedPlaylist);

            return normalizedContent.getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("Failed to normalize playlist Error: {}", e.getMessage(), e);
            throw new PlaylistNormalizeContentException("Failed to normalize playlist", e);
        }
    }


    private MasterPlaylist normalizeMasterPlaylist(MasterPlaylist playlist) {
        List<Variant> normalizedVariants = playlist.variants().stream()
                                                   .map(this::normalizeVariant)
                                                   .distinct()
                                                   .sorted(Comparator.comparing(Variant::bandwidth).reversed())
                                                   .toList();

        return MasterPlaylist.builder()
                             .from(playlist)
                             .variants(normalizedVariants)
                             .build();
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

