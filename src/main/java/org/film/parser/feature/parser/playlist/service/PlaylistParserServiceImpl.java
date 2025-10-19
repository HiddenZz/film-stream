package org.film.parser.feature.parser.playlist.service;

import org.film.parser.feature.parser.playlist.client.PlaylistParserClient;
import org.film.parser.feature.parser.playlist.data.AvailablePlayer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Map.ofEntries;

@Service
public class PlaylistParserServiceImpl implements PlaylistParserService {

    private final PlaylistParserClient parserClient;
    private final MasterPlaylistParserService service;

    public PlaylistParserServiceImpl(PlaylistParserClient parserClient, LumexMasterPlaylistParserService service ) {
        this.parserClient = parserClient;
        this.service = service;
    }

    @Override
    public String parseMasterPlaylist(long id) {
        final List<AvailablePlayer> availablePlayers = parserClient.moviePlaylist(id);

        if(availablePlayers == null || availablePlayers.isEmpty()) {
            throw new RuntimeException("List master playlist not found");
        }

        final AvailablePlayer player = availablePlayers.stream().filter(pl ->pl.name().equals("videocdn")).findFirst().orElse(null);
        service.parse(player.iframe(), id);


        final Map<String, AvailablePlayer> players = availablePlayers.stream().collect(Collectors.toMap(
                AvailablePlayer::name,
                Function.identity(),
                (existing, replacement) -> existing
        ));


        return "";
    }
}
