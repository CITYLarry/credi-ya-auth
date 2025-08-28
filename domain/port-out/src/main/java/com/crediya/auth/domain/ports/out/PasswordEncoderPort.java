package com.crediya.auth.domain.ports.out;

/**
 * An outbound port that defines the contract for a password encoding service.
 */
public interface PasswordEncoderPort {

    /**
     * Encodes a raw password into a secure hash.
     *
     * @param rawPassword The plain-text password to encode.
     * @return The resulting password hash as a String.
     */
    String encode(String rawPassword);

    /**
     * Verifies if a raw password matches an encoded password.
     *
     * @param rawPassword The plain-text password provided during login.
     * @param encodedPassword The stored password hash from the database.
     * @return true if the passwords match, false otherwise.
     */
    boolean matches(String rawPassword, String encodedPassword);
}
