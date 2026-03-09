package com.github.vityan55.musicapp.web.artist.dto;

public record ArtistDto (
        Long artistId,
        Long userId,
        String artistName,
        String description
) {
}