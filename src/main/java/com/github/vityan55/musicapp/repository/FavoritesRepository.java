package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    @Query("""
        SELECT DISTINCT f FROM Favorites f
        JOIN FETCH f.track t
        JOIN FETCH t.mainArtist
        LEFT JOIN FETCH t.feats feat
        LEFT JOIN FETCH feat.artist
        WHERE f.user.id = :userId
        """)
    List<Favorites> findAllByUserId(Long userId);

    void deleteByUserIdAndTrackId(Long userId, Long trackId);
}
