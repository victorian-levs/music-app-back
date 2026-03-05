package com.github.vityan55.musicapp.web.track.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTrackRequest(
        @NotBlank @NotNull String title
) {}
