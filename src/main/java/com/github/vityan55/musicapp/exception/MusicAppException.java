package com.github.vityan55.musicapp.exception;

import org.springframework.http.HttpStatus;

public class MusicAppException extends RuntimeException {
    private final HttpStatus httpStatus;

    public MusicAppException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public MusicAppException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public MusicAppException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public MusicAppException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public MusicAppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus httpStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
