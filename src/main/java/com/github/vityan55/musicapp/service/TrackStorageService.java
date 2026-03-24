package com.github.vityan55.musicapp.service;

import com.github.vityan55.musicapp.config.storage.BucketType;
import com.github.vityan55.musicapp.entity.Track;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.ArtistRepository;
import com.github.vityan55.musicapp.repository.TrackRepository;
import com.github.vityan55.musicapp.web.track.dto.CreateTrackUploadUrlRequest;
import com.github.vityan55.musicapp.web.track.dto.TrackFileMetaData;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackStorageService {

    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final MinioStorageService minioStorageService;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp3", "wav", "flac");

    public String generateUploadURL(String objectKey, Long userId, CreateTrackUploadUrlRequest request) {
        log.info("Generating upload url for track with key {}", objectKey);

        artistRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn("Artist for user with id {} not found", userId);
            return new MusicAppException("Artist not found", HttpStatus.NOT_FOUND);
        });

        if (request.size() > 20 * 1024 * 1024) {
            log.warn("Generating upload url failed for user with id {}. File too large", userId);
            throw new MusicAppException("File too large", HttpStatus.BAD_REQUEST);
        }

        validateExtension(request.filename());

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

    public TrackFileMetaData validateFile(String objectKey) {
        log.info("Validating file with key {}", objectKey);

        StatObjectResponse response = minioStorageService.getResponse(objectKey);

        if (response.size() == 0) {
            log.warn("Error. Empty file {}", objectKey);
            throw new MusicAppException("Empty file", HttpStatus.BAD_REQUEST);
        }

        if (response.contentType() == null || !response.contentType().startsWith("audio/")) {
            log.warn("Error. Invalid file type for file with key {}", objectKey);
            throw new MusicAppException("Invalid file type", HttpStatus.BAD_REQUEST);
        }

        if (response.size() > 20 * 1024 * 1024) {
            log.warn("Error. File with key {} too large", objectKey);
            throw new MusicAppException("File too large", HttpStatus.BAD_REQUEST);
        }

        return new TrackFileMetaData(response.size(), response.contentType());
    }

    public void validateExtension(String fileName) {
        log.info("Validating extension for file {}", fileName);

        if (!fileName.contains(".")) {
            log.warn("Error. Invalid filename {}", fileName);
            throw new MusicAppException("Invalid filename", HttpStatus.BAD_REQUEST);
        }

        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            log.warn("Error. Invalid file extension {}", fileName);
            throw new MusicAppException("Invalid file extension", HttpStatus.BAD_REQUEST);
        }
    }
}