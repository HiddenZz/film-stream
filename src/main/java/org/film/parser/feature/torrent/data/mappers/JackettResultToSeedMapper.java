package org.film.parser.feature.torrent.data.mappers;


import org.film.parser.core.mapper.MapperConfiguration;
import org.film.parser.feature.torrent.data.JackettResult;
import org.film.parser.feature.torrent.data.Seed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface JackettResultToSeedMapper {

    @Mapping(source = "cacheGuid", target = "guid")
    @Mapping(source = "link", target = "externalLink")
    Seed fromJackett(JackettResult result);

    List<Seed> fromJackett(List<JackettResult> results);

}
