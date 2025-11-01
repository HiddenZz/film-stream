package org.film.parser.feature.parser.playlist.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MasterPlaylistParserResolver {

    private final Map<String, MasterPlaylistParserService> masterParsers;

    public MasterPlaylistParserResolver(List<MasterPlaylistParserService> masterParsers) {
        this.masterParsers = masterParsers.stream()
                                          .collect(Collectors.toMap(MasterPlaylistParserService::getName, Function.identity()));
    }

    public MasterPlaylistParserService resolveMasterParser(String name) {
        return masterParsers.get(name);
    }

}
