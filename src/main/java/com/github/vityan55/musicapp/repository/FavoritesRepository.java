package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {}
