package com.github.vityan55.musicapp.config.storage;

import com.github.vityan55.musicapp.exception.MusicAppException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
public class StorageProperties {

    private Map<BucketType, String> buckets;

    public String getBucket(BucketType type) {
        String bucket = buckets.get(type);

        if (bucket == null) {
            throw new MusicAppException("Bucket not configured for type: " + type, HttpStatus.BAD_REQUEST);
        }

        return bucket;
    }
}