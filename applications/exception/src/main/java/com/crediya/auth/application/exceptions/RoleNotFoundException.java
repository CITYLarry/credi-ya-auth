package com.crediya.auth.application.exceptions;

/**
 * A custom business exception thrown when a user registration is attempted
 * with a role name that does not exist in the system.
 */
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
