package com.github.vityan55.musicapp.repository.specification;

import com.github.vityan55.musicapp.entity.Track;
import org.springframework.data.jpa.domain.Specification;

public interface TrackSpecification {

    static Specification<Track> withFilter(TrackFilter filter) {
        return Specification.where(contains("title", filter.getText()));
    }

    private static Specification<Track> contains(String fieldName, String keyword) {
        return ((root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(root.get(fieldName), "%" + keyword + "%");
        });
    }
}
