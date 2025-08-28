package com.crediya.auth.application.ports.in;

import java.util.Objects;

/**
 * Represents a command to log in a user.
 */
public record LoginCommand(
        String email,
        String password
) {
    /**
     * Compact constructor to enforce invariants.
     * Ensures that a LoginCommand can never be instantiated in an invalid state.
     */
    public LoginCommand {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        Objects.requireNonNull(password, "Password cannot be null.");
    }
}
