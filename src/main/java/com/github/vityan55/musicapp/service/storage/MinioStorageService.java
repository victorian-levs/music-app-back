package com.github.vityan55.musicapp.service.storage;

import com.github.vityan55.musicapp.config.storage.BucketType;
import com.github.vityan55.musicapp.config.storage.StorageProperties;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.TrackRepository;
import com.github.vityan55.musicapp.repository.UserRepository;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService {

    private final StorageProperties storageProperties;
    private final MinioClient minioClient;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    public String generateUploadURL(String objectKey, BucketType type) {
        log.info("Generating upload url for object with key {}", objectKey);

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(storageProperties.getBucket(type))
                            .object(objectKey)
                            .expiry(10 * 60)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to generate upload URL for object with key {}", objectKey);
            throw new MusicAppException("Storage error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String generateDownloadUrl(String objectKey, BucketType type) {
        log.info("Generating download url for object with key {}", objectKey);

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(storageProperties.getBucket(type))
                            .object(objectKey)
                            .expiry(5 * 60)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to generate download URL for object with objectKey {}", objectKey);
            throw new MusicAppException("Storage error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanup(){
        cleanupBucket(BucketType.TRACKS, new HashSet<>(trackRepository.findAllFileKeys()));
        cleanupBucket(BucketType.AVATARS, new HashSet<>(userRepository.findAllFileKeys()));
    }

    public StatObjectResponse getResponse(String objectKey) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(storageProperties.getBucket(BucketType.TRACKS))
                            .object(objectKey)
                            .build());

        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                log.warn("Error. File {} not found in storage", objectKey);
                throw new MusicAppException("File not found in storage", HttpStatus.BAD_REQUEST);
            }
            log.warn("Error while checking file with key {} in storage", objectKey);
            throw new MusicAppException("Storage error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.warn("Error while checking file {} in storage", objectKey);
            throw new MusicAppException("Storage error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public InputStream getObject(String objectKey, BucketType type) {
        log.info("Getting object with objectKey {}", objectKey);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(storageProperties.getBucket(type))
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Error while getting object {}", objectKey);
            throw new MusicAppException("Storage error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void delete(String objectKey, BucketType type) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(storageProperties.getBucket(type))
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Error while delete file with key {}", objectKey);
            throw new MusicAppException("Error while delete file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void cleanupBucket(BucketType type, Set<String> usedKeys) {
        log.info("Starting cleanup of orphan files");

        try {
            Iterable<Result<Item>> minioObjects = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(storageProperties.getBucket(type))
                            .build()
            );

            for (Result<Item> result : minioObjects) {
                Item item = result.get();
                String objectKey = item.objectName();

                if (item.lastModified().isBefore(ChronoZonedDateTime.from(
                        Instant.now().minus(1, ChronoUnit.HOURS))) &&
                        !usedKeys.contains(objectKey)
                ) {
                    log.info("Deleting orphan file: {}", objectKey);

                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(storageProperties.getBucket(type))
                                    .object(objectKey)
                                    .build()
                    );

                }

            }
        } catch (Exception e) {
            log.warn("Error during cleanup", e);
        }
    }
}