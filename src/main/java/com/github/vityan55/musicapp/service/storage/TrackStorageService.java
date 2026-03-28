package com.github.vityan55.musicapp.service.storage;

import com.github.vityan55.musicapp.config.storage.BucketType;
import com.github.vityan55.musicapp.entity.Track;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.ArtistRepository;
import com.github.vityan55.musicapp.repository.TrackRepository;
import com.github.vityan55.musicapp.service.validation.AudioFileValidator;
import com.github.vityan55.musicapp.web.track.dto.CreateTrackUploadUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackStorageService {

    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final MinioStorageService minioStorageService;
    private final AudioFileValidator audioFileValidator;

    public String generateUploadURL(String objectKey, Long userId, CreateTrackUploadUrlRequest request) {
        log.info("Generating upload url for track with key {}", objectKey);

        artistRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Artist for user with id {} not found", userId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        audioFileValidator.validateUpload(request.filename(), request.size());

        return minioStorageService.generateUploadURL(objectKey, BucketType.TRACKS);
    }

    public String generateDownloadUrl(Long trackId) {
        log.info("Generating download url for track with id {}", trackId);

        Track track = trackRepository.findById(trackId).orElseThrow(() -> {
            log.warn("Generate download url failed. Track with id {} not found", trackId);
            return new MusicAppException("Track not found", HttpStatus.NOT_FOUND);
        });

        return minioStorageService.generateDownloadUrl(track.getFileKey(), BucketType.TRACKS);
    }
}