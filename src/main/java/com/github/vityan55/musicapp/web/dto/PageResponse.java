package com.github.vityan55.musicapp.web.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> data,
    Integer totalPages
) {}