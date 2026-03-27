package com.github.vityan55.musicapp.service.track;


import com.github.vityan55.musicapp.entity.Artist;
import com.github.vityan55.musicapp.entity.Feat;
import com.github.vityan55.musicapp.entity.Track;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.ArtistRepository;
import com.github.vityan55.musicapp.repository.FeatRepository;
import com.github.vityan55.musicapp.repository.TrackRepository;
import com.github.vityan55.musicapp.repository.specification.TrackFilter;
import com.github.vityan55.musicapp.repository.specification.TrackSpecification;
import com.github.vityan55.musicapp.service.data.AudioMetadataService;
import com.github.vityan55.musicapp.service.storage.MinioStorageService;
import com.github.vityan55.musicapp.service.storage.TrackStorageService;
import com.github.vityan55.musicapp.web.track.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackService {

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final FeatRepository featRepository;
    private final TrackStorageService trackStorageService;
    private final AudioMetadataService audioMetadataService;

    public Page<Track> findAll(Pageable pageable) {
        log.info("Find all tracks by pageable");
        return trackRepository.findAll(pageable);
    }

    public Page<Track> filter(TrackFilter filter, Pageable pageable) {
        log.info("Filter tracks by data: {}", filter);
        return trackRepository.findAll(TrackSpecification.withFilter(filter), pageable);
    }

    @Transactional
    public TrackDto create(CreateTrackRequest request, Long userId) {
        log.info("Create new track");

        Artist mainArtist = artistRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Create track failed. Artist not found by ID: {}", userId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        trackStorageService.validateFile(request.fileKey());
        int duration = audioMetadataService.getDuration(request.fileKey());

        Track track = Track.builder()
                .title(request.title())
                .fileKey(request.fileKey())
                .releaseDate(request.releaseDate())
                .mainArtist(mainArtist)
                .durationMs(duration)
                .build();

        Track newTrack = trackRepository.save(track);

        if (request.featArtistIds() != null && !request.featArtistIds().isEmpty()) {
            log.info("Finding feat artists for track with ID: {}", newTrack.getId());
            List<Artist> featArtists = artistRepository.findAllById(request.featArtistIds());

            if (featArtists.size() != request.featArtistIds().size()) {
                log.warn("Create track failed. Some artists not found");
                throw new MusicAppException("Some artists not found", HttpStatus.NOT_FOUND);
            }

            for (Artist artist : featArtists) {
                if (Objects.equals(artist.getId(), mainArtist.getId())) {
                    continue;
                }

                Feat feat = Feat.builder()
                        .artist(artist)
                        .track(newTrack)
                        .build();

                featRepository.save(feat);
            }
        }

        return new TrackDto(
                newTrack.getId(),
                newTrack.getTitle(),
                new TrackArtistDto(mainArtist.getId(), mainArtist.getArtistName()),
                featRepository.findArtistsNameAndIdByTrackId(newTrack.getId()),
                newTrack.getDurationMs(),
                newTrack.getReleaseDate()
        );
    }

    @Transactional
    public TrackDto updateTrack(
            Long userId,
            UpdateTrackRequest request,
            Long trackId
    ) {
        log.info("Update track by track ID: {}", trackId);

        Artist mainArtist = artistRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Update track failed. Artist not found by ID: {}", userId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        Track track = trackRepository.findById(trackId).orElseThrow(() -> {
            log.warn("Update track failed. Track not found by id: {}", trackId);
            return new MusicAppException("Track not found", HttpStatus.NOT_FOUND);
        });

        if (!track.getMainArtist().getId().equals(mainArtist.getId())){
            log.warn("Update track failed. Track with id {} not found by artist id: {}", trackId, mainArtist.getId());
            throw new MusicAppException("Track not found", HttpStatus.NOT_FOUND);
        }

        track.setTitle(request.title());

        return new TrackDto(
                trackId,
                track.getTitle(),
                new TrackArtistDto(mainArtist.getId(), mainArtist.getArtistName()),
                featRepository.findArtistsNameAndIdByTrackId(track.getId()),
                track.getDurationMs(),
                track.getReleaseDate()
        );
    }

    @Transactional
    public void delete(Long userId, Long trackId) {
        log.info("Delete track with ID: {}", trackId);

        Artist artist = artistRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Delete track failed. Artist not found by user id: {}", userId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        Track track = trackRepository.findById(trackId).orElseThrow(() -> {
            log.warn("Delete track failed. Track not found by id: {}", trackId);
            return new MusicAppException("Track not found", HttpStatus.NOT_FOUND);
        });

        if (!track.getMainArtist().getId().equals(artist.getId())) {
            log.warn("Delete track failed. Track with id {} not found by artist id: {}", trackId, artist.getId());
            throw new MusicAppException("Track not found", HttpStatus.NOT_FOUND);
        }

        trackRepository.deleteById(trackId);
    }
}