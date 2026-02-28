package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {}
