package com.github.vityan55.musicapp.web.track.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateTrackRequest(
        @NotNull @NotBlank String title,
        @NotNull @NotBlank String fileKey,
        @NotNull Long durationMs,
        @NotNull LocalDate releaseDate,
        List<Long> featArtistIds
) {
}
