package com.github.vityan55.musicapp.web.superadmin.dto;

import com.github.vityan55.musicapp.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record CreateAdminRequest(
        @NotBlank String email,
        @NotBlank @ValidPassword String password,
        @NotBlank String adminName
) {
}
