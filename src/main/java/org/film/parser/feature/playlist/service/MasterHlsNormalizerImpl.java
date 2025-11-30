package org.film.parser.feature.playlist.service;

import io.lindstrom.m3u8.model.*;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.ParsingMode;
import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.playlist.data.MasterMediaNormalized;
import org.film.parser.feature.playlist.data.MediaVariant;
import org.film.parser.feature.playlist.data.exceptions.PlaylistNormalizeContentException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Primary
public class MasterHlsNormalizerImpl implements MasterHlsNormalizer {

    private final MasterPlaylistParser parser;

    public MasterHlsNormalizerImpl() {
        this.parser = new MasterPlaylistParser(ParsingMode.LENIENT);
    }

    @Override
    public MasterMediaNormalized normalize(byte[] media, long contentId) {
        try {
            String content = new String(media, StandardCharsets.UTF_8);
            final MasterPlaylist playlist = parser.readPlaylist(content);

            // Collectors for normalized data
            List<MediaVariant> mediaVariants = new ArrayList<>();

            List<AlternativeRendition> normalizedRenditions = normalizeAlternativeRenditions(
                    playlist.alternativeRenditions(),
                    mediaVariants,
                    contentId
            );

            List<Variant> normalizedVariants = normalizeVariants(
                    playlist.variants(),
                    mediaVariants,
                    contentId
            );

            List<IFrameVariant> normalizedIFrameVariants = normalizeIFrameVariants(
                    playlist.iFrameVariants(),
                    mediaVariants,
                    contentId
            );

            // Build normalized master playlist
            final MasterPlaylist normalizedPlaylist = MasterPlaylist.builder()
                                                                    .from(playlist)
                                                                    .alternativeRenditions(normalizedRenditions)
                                                                    .variants(normalizedVariants)
                                                                    .iFrameVariants(normalizedIFrameVariants)
                                                                    .build();

            String normalizedContent = parser.writePlaylistAsString(normalizedPlaylist);


            return new MasterMediaNormalized(
                    normalizedContent.getBytes(StandardCharsets.UTF_8),
                    mediaVariants
            );

        } catch (Exception e) {
            log.error("Failed to normalize master playlist: {}", e.getMessage(), e);
            throw new PlaylistNormalizeContentException("Failed to normalize master playlist", e);
        }
    }

    private List<AlternativeRendition> normalizeAlternativeRenditions(
            List<AlternativeRendition> renditions,
            List<MediaVariant> mediaVariants,
            long contentId
    ) {
        List<AlternativeRendition> normalized = new ArrayList<>();
        Map<MediaType, AtomicInteger> counters = new HashMap<>();

        for (AlternativeRendition rendition : renditions) {
            if (rendition.uri().isEmpty()) {

                normalized.add(rendition);
                continue;
            }

            String originalUri = rendition.uri().get();

            AtomicInteger counter = counters.computeIfAbsent(
                    rendition.type(),
                    k -> new AtomicInteger(0)
            );

            int index = counter.getAndIncrement();
            String relativePath = generateAlternativePartNormalizedPath(rendition.type(), rendition, index);
            String normalizedPath = generateFullPath(contentId, relativePath);


            // Create MediaVariant entry (store relative path for DB lookup)
            MediaVariant variant = MediaVariant.builder()
                                               .mediaType(MediaVariant.MediaType.valueOf(rendition.type().name()))
                                               .resolution(0) // Not applicable for audio/subtitles
                                               .fallbackUrl(originalUri)
                                               .normalizedPath(normalizedPath)
                                               .metadata(MediaVariant.MediaMetadata.builder()
                                                                                   .groupId(rendition.groupId())
                                                                                   .name(rendition.name())
                                                                                   .language(rendition.language()
                                                                                                      .orElse(null))
                                                                                   .isDefault(rendition.autoSelect()
                                                                                                       .orElse(false))
                                                                                   .build())
                                               .build();

            mediaVariants.add(variant);


            AlternativeRendition normalizedRendition = AlternativeRendition.builder()
                                                                           .from(rendition)
                                                                           .uri(normalizedPath)
                                                                           .build();

            normalized.add(normalizedRendition);
        }

        return normalized;
    }

    private List<Variant> normalizeVariants(
            List<Variant> variants,
            List<MediaVariant> mediaVariants,
            long contentId
    ) {
        List<Variant> normalized = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        for (Variant variant : variants) {
            String originalUri = variant.uri();
            int resolution = variant.resolution()
                                    .map(Resolution::height)
                                    .orElse(0);

            String relativePath = resolution > 0 ? "video/" + resolution + "/index.m3u8" : "video/unknown_" + counter.getAndIncrement() + "/index.m3u8";


            String fullPath = generateFullPath(contentId, relativePath);


            // Create MediaVariant entry (store relative path for DB lookup)
            MediaVariant mediaVariant = MediaVariant.builder()
                                                    .mediaType(MediaVariant.MediaType.VIDEO)
                                                    .resolution(resolution)
                                                    .fallbackUrl(originalUri)
                                                    .normalizedPath(fullPath)
                                                    .metadata(MediaVariant.MediaMetadata.builder()
                                                                                        .bandwidth(variant.bandwidth())
                                                                                        .codecs(variant.codecs()
                                                                                                       .isEmpty() ? null : String.join(",", variant.codecs()))
                                                                                        .build())
                                                    .build();

            mediaVariants.add(mediaVariant);

            // Update variant with full normalized URI (with /content/{contentId}/ prefix)
            Variant normalizedVariant = Variant.builder()
                                               .from(variant)
                                               .uri(fullPath)
                                               .build();

            normalized.add(normalizedVariant);
        }

        return normalized;
    }

    private List<IFrameVariant> normalizeIFrameVariants(
            List<IFrameVariant> iFrameVariants,
            List<MediaVariant> mediaVariants,
            long contentId
    ) {
        List<IFrameVariant> normalized = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        for (IFrameVariant iFrame : iFrameVariants) {
            String originalUri = iFrame.uri();
            int resolution = iFrame.resolution()
                                   .map(Resolution::height)
                                   .orElse(0);

            String relativePath = resolution > 0 ? "iframe/" + resolution + "/index.m3u8" : "iframe/unknown_" + counter.getAndIncrement() + "/index.m3u8";


            String fullPath = generateFullPath(contentId, relativePath);


            MediaVariant mediaVariant = MediaVariant.builder()
                                                    .mediaType(MediaVariant.MediaType.IFRAME)
                                                    .resolution(resolution)
                                                    .fallbackUrl(originalUri)
                                                    .normalizedPath(fullPath)
                                                    .metadata(MediaVariant.MediaMetadata.builder()
                                                                                        .bandwidth(iFrame.bandwidth())
                                                                                        .codecs(iFrame.codecs()
                                                                                                      .isEmpty() ? null : String.join(",", iFrame.codecs()))
                                                                                        .build())
                                                    .build();

            mediaVariants.add(mediaVariant);

            IFrameVariant normalizedIFrame = IFrameVariant.builder()
                                                          .from(iFrame)
                                                          .uri(fullPath)
                                                          .build();

            normalized.add(normalizedIFrame);
        }

        return normalized;
    }

    private String generateAlternativePartNormalizedPath(MediaType mediaType, AlternativeRendition rendition,
                                                         int index) {
        String type = mediaType.name().toLowerCase();
        String groupId = sanitizePathSegment(rendition.groupId());

        return type + "/" + groupId + "/" + index + "/index.m3u8";
    }

    private String sanitizePathSegment(String segment) {
        return segment.replaceAll("[^a-zA-Z0-9-_]", "_");
    }


    private String generateFullPath(long contentId, String relativePath) {
        return "/content/" + contentId + "/" + relativePath;
    }
}
