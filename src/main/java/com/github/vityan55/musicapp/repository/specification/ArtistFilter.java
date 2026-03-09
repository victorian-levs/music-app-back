package com.github.vityan55.musicapp.repository.specification;

import lombok.Data;

@Data
public class ArtistFilter {
    private String text;

    private Integer pageSize;

    private Integer pageNumber;
}
