package org.film.parser.feature.parser.playlist.service;

import lombok.extern.slf4j.Slf4j;
import org.film.parser.feature.parser.playlist.client.PlaylistParserClient;
import org.film.parser.feature.parser.playlist.data.AvailablePlayer;
import org.film.parser.feature.parser.playlist.data.ParsedMasterMedia;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PlaylistParserServiceImpl implements PlaylistParserService {

    private final PlaylistParserClient parserClient;
    private final MasterPlaylistParserResolver masterPlaylistparserResolver;

    public PlaylistParserServiceImpl(PlaylistParserClient parserClient,
                                     MasterPlaylistParserResolver masterPlaylistparserResolver) {
        this.parserClient = parserClient;
        this.masterPlaylistparserResolver = masterPlaylistparserResolver;
    }


    @Override
    public ParsedMasterMedia parseMasterPlaylist(long id) {
        final List<AvailablePlayer> availablePlayers = parserClient.moviePlaylist(id);

        if (availablePlayers == null || availablePlayers.isEmpty()) {
            throw new RuntimeException("List master playlist not found");
        }


        return availablePlayers.stream()
                               .map(player -> {
                                   MasterPlaylistParserService parser = masterPlaylistparserResolver.resolveMasterParser(player.name());
                                   if (parser == null) return null;

                                   return tryParse(parser, player, id);
                               })
                               .filter(Objects::nonNull)
                               .findFirst()
                               .orElseThrow(() -> new RuntimeException("All parsers failed"));

    }

    private ParsedMasterMedia tryParse(MasterPlaylistParserService parser, AvailablePlayer player, long id) {
        try {
            return parser.parse(player.iframe(), id);
        } catch (Exception e) {
            log.warn("Failed to parse with player: {}. Error: {}", player.name(), e.getMessage());
            return null;
        }
    }

}
