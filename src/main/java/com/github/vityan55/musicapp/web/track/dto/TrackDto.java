package com.github.vityan55.musicapp.web.track.dto;

import java.time.LocalDate;
import java.util.List;

public record TrackDto(
        Long id,
        String title,
        TrackArtistDto mainArtist,
        List<TrackArtistDto> featArtists,
        int durationMs,
        LocalDate releaseDate
) {}
