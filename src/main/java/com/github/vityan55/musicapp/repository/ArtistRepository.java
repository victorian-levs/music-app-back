package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {}
