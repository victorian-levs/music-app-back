package com.github.vityan55.musicapp.repository;

import com.github.vityan55.musicapp.entity.Feat;
import com.github.vityan55.musicapp.web.track.dto.TrackArtistDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeatRepository extends JpaRepository<Feat, Long> {
    @Query("SELECT new com.github.vityan55.musicapp.web.track.dto.TrackArtistDto (" +
            "f.artist.id, f.artist.artistName" +
            ") " +
            "FROM Feat f " +
            "WHERE f.track.id = :trackId")
    List<TrackArtistDto> findArtistsNameAndIdByTrackId(@Param("trackId") Long trackId);

    void deleteAllByTrackId(Long trackId);
}
