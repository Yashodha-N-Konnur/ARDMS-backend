package com.ardms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all ARDMS application exceptions.
 */
@Getter
public class ArdmsException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    public ArdmsException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ArdmsException(String message, HttpStatus httpStatus, String errorCode, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
