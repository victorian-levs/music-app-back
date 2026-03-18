package com.github.vityan55.musicapp.service;

import com.github.vityan55.musicapp.config.MinioConfig;
import com.github.vityan55.musicapp.exception.MusicAppException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;

    private final String bucket = "tracks";

    public String generateUploadURL(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(10 * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }

    public String generateDownloadUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(5 * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }
}