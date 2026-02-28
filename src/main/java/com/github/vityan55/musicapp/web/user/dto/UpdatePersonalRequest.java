package com.github.vityan55.musicapp.web.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePersonalRequest(@NotBlank @NotNull String username) {}
