package com.github.vityan55.musicapp.web.user.dto;

public record UserWithTypeDto(
        Long id,
        String email,
        String username,
        boolean isArtist
) {}
