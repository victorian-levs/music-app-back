package com.github.vityan55.musicapp.web.artist.dto;

public record UpdateArtistRequest(
        String artistName,
        String description
) {
}
