package com.github.vityan55.musicapp.web.track.dto;

public record CreateTrackUploadUrlRequest(
        String filename,
        Long size
) {
}
