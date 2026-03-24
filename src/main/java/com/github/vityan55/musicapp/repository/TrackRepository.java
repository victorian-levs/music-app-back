package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long>, JpaSpecificationExecutor<Track> {
    @EntityGraph(attributePaths = {"feats", "feats.artist", "mainArtist"})
    Page<Track> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"feats", "feats.artist", "mainArtist"})
    Page<Track> findAll(Specification<Track> spec, Pageable pageable);

    @Query("SELECT t.fileKey FROM Track t")
    List<String> findAllFileKeys();
}
