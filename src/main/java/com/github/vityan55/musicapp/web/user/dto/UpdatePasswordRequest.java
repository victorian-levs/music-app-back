package com.github.vityan55.musicapp.web.user.dto;

import com.github.vityan55.musicapp.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(@ValidPassword String newPassword,
                                    @NotNull @NotBlank String oldPassword) {}
