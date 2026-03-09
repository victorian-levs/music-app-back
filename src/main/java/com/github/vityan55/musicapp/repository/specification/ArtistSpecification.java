package com.github.vityan55.musicapp.repository.specification;

import com.github.vityan55.musicapp.entity.Artist;
import org.springframework.data.jpa.domain.Specification;

public interface ArtistSpecification {
    static Specification<Artist> withFilter(ArtistFilter filter) {
        return Specification.where(contains("artistName", filter.getText()));
    }

    private static Specification<Artist> contains(String fieldName, String keyword) {
        return ((root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(root.get(fieldName), "%" + keyword + "%");
        });
    }
}
