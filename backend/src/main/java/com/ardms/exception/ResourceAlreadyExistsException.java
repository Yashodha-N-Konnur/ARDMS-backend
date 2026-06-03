package com.ardms.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends ArdmsException {

    public ResourceAlreadyExistsException(String resource, String field, Object value) {
        super(
            String.format("%s already exists with %s: '%s'", resource, field, value),
            HttpStatus.CONFLICT,
            "RESOURCE_ALREADY_EXISTS"
        );
    }
}
