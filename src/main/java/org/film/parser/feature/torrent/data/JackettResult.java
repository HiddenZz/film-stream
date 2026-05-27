package org.film.parser.feature.torrent.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JackettResult {
    private String cacheGuid;
    private String tracker;
    private String trackerId;
    private String trackerType;
    private String categoryDesc;
    private String title;
    private String guid;
    private String link;
    private String details;
    private String publishDate;
    private List<Integer> category;
    private Long size;
    private Integer seeders;
    private Integer peers;
    private Double gain;
    private long tmdbId;
}
