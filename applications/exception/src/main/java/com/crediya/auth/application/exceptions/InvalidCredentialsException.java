package com.crediya.auth.application.exceptions;

/**
 * A custom business exception thrown when a login attempt fails due to incorrect email or password.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
