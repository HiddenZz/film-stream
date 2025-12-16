package org.film.parser.feature.parser.playlist.service;

import lombok.extern.slf4j.Slf4j;
import org.film.parser.core.util.resolver.ServiceResolver;
import org.film.parser.feature.parser.playlist.client.PlaylistParserClient;
import org.film.parser.feature.parser.playlist.data.AvailablePlayer;
import org.film.parser.feature.parser.playlist.data.ParsedContentPlaylistMedia;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PlaylistParserServiceImpl implements PlaylistParserService {

    private final PlaylistParserClient parserClient;
    private final ServiceResolver<MasterPlaylistParser> masterPlaylistParserResolver;
    private final ServiceResolver<ContentPlaylistParserService> contentPlaylistParserResolver;

    public PlaylistParserServiceImpl(PlaylistParserClient parserClient,
                                     ServiceResolver<MasterPlaylistParser> masterPlaylistParserResolver,
                                     ServiceResolver<ContentPlaylistParserService> contentPlaylistParserResolver) {
        this.parserClient = parserClient;
        this.masterPlaylistParserResolver = masterPlaylistParserResolver;
        this.contentPlaylistParserResolver = contentPlaylistParserResolver;
    }


    @Override
    public ParsedMasterMedia parseMasterPlaylist(long id) {
        final List<AvailablePlayer> availablePlayers = parserClient.moviePlaylist(id);

        if (availablePlayers == null || availablePlayers.isEmpty()) {
            throw new RuntimeException("List master playlist not found");
        }

        return availablePlayers.stream()
                               .map(player -> {
                                   MasterPlaylistParser parser = masterPlaylistParserResolver.resolve(player.name());
                                   if (parser == null) return null;

                                   return tryParse(parser, player, id);
                               })
                               .filter(Objects::nonNull)
                               .findFirst()
                               .orElseThrow(() -> new RuntimeException("All parsers failed"));

    }

    @Override
    public ParsedContentPlaylistMedia parseContentPlaylist(long id, String masterHlsUrl, String contentUrl,
                                                           String serviceName) {
        return contentPlaylistParserResolver
                .resolve(serviceName)
                .parse(
                        masterHlsUrl,
                        contentUrl
                );
    }

    private ParsedMasterMedia tryParse(MasterPlaylistParser parser, AvailablePlayer player, long id) {
        try {
            return parser.parse(player.iframe(), id);
        } catch (Exception e) {
            log.error("Failed to parse with player: {}", player.name(), e);
            return null;
        }
    }
}
