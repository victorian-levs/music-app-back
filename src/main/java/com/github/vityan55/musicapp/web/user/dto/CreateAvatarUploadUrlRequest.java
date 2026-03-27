package com.github.vityan55.musicapp.web.user.dto;

public record CreateAvatarUploadUrlRequest(
        String filename,
        Long size
) {
}