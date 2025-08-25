package com.crediya.auth.application.exceptions;

/**
 * A custom business exception thrown when attempting to register a user with an email that already exists in the system.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
