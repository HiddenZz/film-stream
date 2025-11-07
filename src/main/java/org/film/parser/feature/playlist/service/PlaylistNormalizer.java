package org.film.parser.feature.playlist.service;


import org.film.parser.feature.playlist.data.MasterMediaNormalized;

public interface PlaylistNormalizer {


    MasterMediaNormalized normalizeMasterPlaylist(byte[] media);
}

