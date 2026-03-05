package com.github.vityan55.musicapp.repository.specification;

import lombok.Data;

@Data
public class TrackFilter {
    private String text;

    private Integer pageSize;

    private Integer pageNumber;
}
