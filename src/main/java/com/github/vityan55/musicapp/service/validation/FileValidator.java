package com.github.vityan55.musicapp.service.validation;

import java.util.Set;

public interface FileValidator {
    void validate(String objectKey);

    Set<String> getAllowedExtension();
    long getMaxSize();

    default boolean isHeaderValid(byte[] header) {
        return true;
    }
}
