package com.github.vityan55.musicapp.service.data;

import com.github.vityan55.musicapp.config.storage.BucketType;
import com.github.vityan55.musicapp.exception.MusicAppException;
import com.github.vityan55.musicapp.service.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudioMetadataService {

    private final MinioStorageService minioStorageService;

    public int getDuration(String objectKey) {
        log.info("Getting duration for objectKey: {}", objectKey);

        File tempFile = null;

        try (InputStream stream = minioStorageService.getObject(objectKey, BucketType.TRACKS);) {

            tempFile = File.createTempFile("audio", ".tmp");

            try (OutputStream out = new FileOutputStream(tempFile)) {
                stream.transferTo(out);
            }

            AudioFile audioFile = AudioFileIO.read(tempFile);

            return audioFile.getAudioHeader().getTrackLength() * 1000;
        } catch (Exception e) {
            log.warn("Error while getting duration");
            throw new MusicAppException("Error while getting duration", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}
