package com.github.vityan55.musicapp.seeder;

import com.github.vityan55.musicapp.config.storage.BucketType;
import com.github.vityan55.musicapp.config.storage.StorageProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageSeeder {

    private final MinioClient minioClient;
    private final StorageProperties storageProperties;

    @PostConstruct
    public void createBuckets() {
        for (BucketType type : BucketType.values()) {
            String bucket = storageProperties.getBucket(type);

            try {
                boolean exists = minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(bucket)
                                .build()
                );

                if (!exists) {
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(bucket)
                                    .build()
                    );
                    log.info("Bucket created {}", bucket);
                } else {
                    log.info("Bucket already exists: {}", bucket);
                }
            } catch (Exception e) {
                log.warn("MiniO problem with bucket: {}: {}", bucket, e.getMessage());
            }
        }
    }
}