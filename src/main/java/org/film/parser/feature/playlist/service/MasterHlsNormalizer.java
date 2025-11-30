package org.film.parser.feature.playlist.service;


import org.film.parser.feature.playlist.data.MasterMediaNormalized;

public interface MasterHlsNormalizer {

    MasterMediaNormalized normalize(byte[] media, long contentId);
}

