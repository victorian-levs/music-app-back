package com.github.vityan55.musicapp.web.auth.dto;

import com.github.vityan55.musicapp.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String email,
        @NotBlank @ValidPassword String password
) {}