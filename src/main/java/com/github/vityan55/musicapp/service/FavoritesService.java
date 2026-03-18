package com.github.vityan55.musicapp.service;

import com.github.vityan55.musicapp.entity.Artist;
import com.github.vityan55.musicapp.entity.Favorites;
import com.github.vityan55.musicapp.entity.Track;
import com.github.vityan55.musicapp.entity.User;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.FavoritesRepository;
import com.github.vityan55.musicapp.repository.TrackRepository;
import com.github.vityan55.musicapp.repository.UserRepository;
import com.github.vityan55.musicapp.web.track.dto.FavoriteTracksDto;
import com.github.vityan55.musicapp.web.track.dto.TrackArtistDto;
import com.github.vityan55.musicapp.web.track.dto.TrackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    public FavoriteTracksDto getLikedTracks(Long userId) {
        log.info("Get liked tracks for user with id {}", userId);

        List<Favorites> favorites = favoritesRepository.findAllByUserId(userId);
        int countOfLikes = favorites.size();

        List<TrackDto> tracks = favorites.stream()
                .map(favorite -> new TrackDto(
                        favorite.getTrack().getId(),
                        favorite.getTrack().getTitle(),
                        new TrackArtistDto(
                                favorite.getTrack().getMainArtist().getId(),
                                favorite.getTrack().getMainArtist().getArtistName()
                        ),
                        favorite.getTrack().getFeats()
                                .stream()
                                .map(feat -> new TrackArtistDto(
                                        feat.getArtist().getId(),
                                        feat.getArtist().getArtistName()
                                ))
                                .toList(),
                        favorite.getTrack().getDurationMs(),
                        favorite.getTrack().getReleaseDate()
                ))
                .toList();

        return new FavoriteTracksDto(countOfLikes, tracks);
    }

    @Transactional
    public void like(Long userId, Long trackId) {
        log.info("Create like from user with id {} to track with id {}", userId, trackId);

        if (userId == null || trackId == null) {
            log.warn("Like track failed for user with id {}. Invalid input provided", userId);
            throw new MusicAppException("Invalid input provided", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User not found by id {}", userId);
            return new MusicAppException("User not found", HttpStatus.NOT_FOUND);
        });

        Track track = trackRepository.findById(trackId).orElseThrow(() -> {
            log.warn("Track not found by id {}", trackId);
            return new MusicAppException("Track not found", HttpStatus.NOT_FOUND);
        });

        Favorites favorites = Favorites.builder()
                .user(user)
                .track(track)
                .build();

        try {
            favoritesRepository.save(favorites);
        } catch (DataIntegrityViolationException e) {
            throw new MusicAppException("Like already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void deleteLike(Long userId, Long trackId) {
        log.info("Deleting like for user with id {} and track with id {}", userId, trackId);

        if (userId == null || trackId == null) {
            log.warn("Delete like failed for user with id {}. Invalid input provided", userId);
            throw new MusicAppException("Invalid input provided", HttpStatus.BAD_REQUEST);
        }

        favoritesRepository.deleteByUserIdAndTrackId(userId, trackId);
    }
}
