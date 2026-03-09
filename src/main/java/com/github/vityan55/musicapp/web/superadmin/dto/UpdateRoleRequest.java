package com.github.vityan55.musicapp.web.superadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
        @NotBlank @NotNull String role
) {
}
