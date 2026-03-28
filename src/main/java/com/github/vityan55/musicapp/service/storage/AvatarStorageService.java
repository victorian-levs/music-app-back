package com.github.vityan55.musicapp.service.storage;

import com.github.vityan55.musicapp.config.storage.BucketType;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.repository.UserRepository;
import com.github.vityan55.musicapp.service.validation.ImageFileValidator;
import com.github.vityan55.musicapp.web.user.dto.CreateAvatarUploadUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarStorageService {
    private final MinioStorageService minioStorageService;
    private final ImageFileValidator imageFileValidator;
    private final UserRepository userRepository;

    public String generateUploadURL(String objectKey, Long userId, CreateAvatarUploadUrlRequest request) {
        log.info("Generating upload url for avatar with key {}", objectKey);

        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id {} not found", userId);
            return new MusicAppException("User not found", HttpStatus.NOT_FOUND);
        });

        imageFileValidator.validateUpload(request.filename(), request.size());

        return minioStorageService.generateUploadURL(objectKey, BucketType.AVATARS);
    }

    public String generateDownloadUrl(String objectKey) {
        log.info("Generating download url for avatar with key {}", objectKey);
        return minioStorageService.generateDownloadUrl(objectKey, BucketType.AVATARS);
    }

    public void delete(String oldAvatarKey, BucketType type) {
        minioStorageService.delete(oldAvatarKey, type);
    }
}