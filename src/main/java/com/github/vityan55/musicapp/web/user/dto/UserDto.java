package com.github.vityan55.musicapp.web.user.dto;

public record UserDto(
        Long id,
        String email,
        String username
) {}