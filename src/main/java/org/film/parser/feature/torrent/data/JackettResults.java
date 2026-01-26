package org.film.parser.feature.torrent.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JackettResults {
    private List<JackettResult> results;
}
