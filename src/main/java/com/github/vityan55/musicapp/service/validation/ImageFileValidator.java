package com.github.vityan55.musicapp.service.validation;

import com.github.vityan55.musicapp.service.storage.MinioStorageService;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageFileValidator implements FileValidator {

    private final MinioStorageService minioStorageService;

    public static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    public static final long MAX_SIZE = 10 * 1024 * 1024;

    @Override
    public void validate(String objectKey) {
        log.info("Validating file with key {}", objectKey);

        StatObjectResponse response = minioStorageService.getResponse(objectKey);

        FileValidationUtils.validateSize(response.size(), MAX_SIZE);
    }

    public void validateUpload(String filename, long size) {
        FileValidationUtils.validateExtension(filename, ALLOWED_EXTENSIONS);
        FileValidationUtils.validateSize(size, MAX_SIZE);
    }

    @Override
    public Set<String> getAllowedExtension() {
        return ALLOWED_EXTENSIONS;
    }

    @Override
    public long getMaxSize() {
        return MAX_SIZE;
    }
}
