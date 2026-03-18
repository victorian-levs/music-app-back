package com.github.vityan55.musicapp.web.track.dto;

import java.util.List;

public record FavoriteTracksDto(
        int countOfLikes,
        List<TrackDto> tracks
) {
}
