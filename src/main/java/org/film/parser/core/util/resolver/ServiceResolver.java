package org.film.parser.core.util.resolver;

import org.film.parser.feature.parser.playlist.service.MasterPlaylistParserService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceResolver<T extends Named> {

    private final Map<String, T> services;

    public ServiceResolver(List<T> services) {
        this.services = services.stream()
                                .collect(Collectors.toMap(Named::getName, Function.identity()));
    }

    public T resolve(String name) {
        return services.get(name);
    }
}
