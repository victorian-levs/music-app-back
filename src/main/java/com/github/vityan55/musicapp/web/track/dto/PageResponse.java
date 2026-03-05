package com.github.vityan55.musicapp.web.track.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> data,
    Integer totalPages
) {}