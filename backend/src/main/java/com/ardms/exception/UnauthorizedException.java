package com.ardms.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ArdmsException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
