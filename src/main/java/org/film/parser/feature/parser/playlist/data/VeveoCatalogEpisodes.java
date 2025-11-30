package org.film.parser.feature.parser.playlist.data;

import java.util.List;

public record VeveoCatalogEpisodes(String m3u8MasterFilePath, List<VeveoEpisodeVariant> episodeVariants) {

    public record VeveoEpisodeVariant(String filepath) {
       
    }
}
