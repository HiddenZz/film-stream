package org.film.parser.feature.playlist.service;

import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import org.film.parser.feature.playlist.data.ContentMediaMeta;
import org.film.parser.feature.playlist.data.ContentPlaylistMedia;
import org.film.parser.feature.playlist.data.MediaVariant;
import org.film.parser.feature.playlist.data.exceptions.PlaylistNormalizeContentException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContentHlsNormalizerImpl implements ContentHlsNormalizer {

    private final MediaPlaylistParser mediaPlaylistParser;

    public ContentHlsNormalizerImpl() {
        this.mediaPlaylistParser = new MediaPlaylistParser();
    }

    @Override
    public ContentPlaylistMedia normalize(byte[] media, MediaVariant mediaVariant, long contentId) {
        try {
            final String content = new String(media, StandardCharsets.UTF_8);
            final String uniqueId = generateUniqueId(mediaVariant.normalizedPath());

            final ArrayList<ContentMediaMeta> mediaMetas = new ArrayList<>();

            final MediaPlaylist mediaPlaylist = mediaPlaylistParser.readPlaylist(content);
            final ArrayList<MediaSegment> normalizedMediaPlaylist = new ArrayList<>();

            for (int i = 0; i < mediaPlaylist.mediaSegments().size(); i++) {
                final MediaSegment segment = mediaPlaylist.mediaSegments().get(i);
                final String path = "/movie/" + contentId + "/segment/" + mediaVariant.mediaType()
                                                                                      .name() + "/" + uniqueId + "/" + "seg-" + i + ".ts";

                normalizedMediaPlaylist.add(MediaSegment.builder()
                                                        .from(segment)
                                                        .uri(path)
                                                        .build());

                mediaMetas.add(ContentMediaMeta.builder()
                                               .contentId(contentId)
                                               .fallbackUrl(segment.uri())
                                               .segmentIndex(i)
                                               .mediaPath(path)
                                               .build());
            }

            final MediaPlaylist normalizedPlaylist = MediaPlaylist.builder()
                                                                  .from(mediaPlaylist)
                                                                  .mediaSegments(normalizedMediaPlaylist)
                                                                  .build();

            return new ContentPlaylistMedia(mediaPlaylistParser.writePlaylistAsBytes(normalizedPlaylist), mediaMetas);

        } catch (Exception e) {
            throw new PlaylistNormalizeContentException("Failed to normalize content HLS playlist for contentId: " + contentId, e);
        }

    }


    private String generateUniqueId(String path) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(path.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
