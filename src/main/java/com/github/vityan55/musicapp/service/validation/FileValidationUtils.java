package com.github.vityan55.musicapp.service.validation;

import com.github.vityan55.musicapp.exception.MusicAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Set;

@Slf4j
public class FileValidationUtils {
    public static String getExtension(String fileName) {
        if (!fileName.contains(".")) {
            log.warn("Error. Invalid filename {}", fileName);
            throw new MusicAppException("Invalid filename", HttpStatus.BAD_REQUEST);
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    public static void validateExtension(String fileName, Set<String> allowed) {
        String ext = getExtension(fileName);

        if (!allowed.contains(ext)) {
            log.warn("Extension is not allowed for file {}", fileName);
            throw new MusicAppException("Invalid file extension", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateSize(long size, long maxSize) {
        if (size > maxSize || size == 0) {
            log.warn("File too large {}", size);
            throw new MusicAppException("File too large", HttpStatus.BAD_REQUEST);
        }
    }
}
