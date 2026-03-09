package com.github.vityan55.musicapp.web.artist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateArtistRequest(
        @NotBlank @NotNull Long userId,
        @NotBlank @NotNull String artistName,
        @NotBlank @NotNull String description
) {
}
