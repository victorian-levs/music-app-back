package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findByUserId(Long userId);
}
