package org.film.parser.feature.parser.playlist.data;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.mapstruct.Mapping;

import java.util.List;




@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LumexContentPlayer(String poster, String kinopoiskId, String contentType, List<LumexContentPlayerMedia> media) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record LumexContentPlayerMedia(String translationName, int maxQuality, String playlist){

    }

}
