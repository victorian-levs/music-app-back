package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrackRepository extends JpaRepository<Track, Long>, JpaSpecificationExecutor<Track> {
    @EntityGraph(attributePaths = {"feats", "feats.artist", "mainArtist"})
    Page<Track> findAll(Pageable pageable);
}
