package com.github.vityan55.musicapp.web;

import com.github.vityan55.musicapp.exception.MusicAppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MusicAppException.class)
    public ResponseEntity<?> handleMusicAppException(MusicAppException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ex.getMessage());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied");
    }
}
