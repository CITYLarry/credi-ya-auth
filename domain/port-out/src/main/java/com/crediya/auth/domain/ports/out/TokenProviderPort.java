package com.crediya.auth.domain.ports.out;

import com.crediya.auth.domain.model.User;

/**
 * An outbound port that defines the contract for a token generation service.
 */
public interface TokenProviderPort {

    /**
     * Generates a security token for a given user.
     *
     * @param user The authenticated User domain object.
     * @return A string representation of the generated token.
     */
    String generateToken(User user);
}
