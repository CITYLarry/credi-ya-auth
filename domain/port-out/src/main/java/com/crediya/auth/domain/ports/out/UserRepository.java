package com.crediya.auth.domain.ports.out;

import com.crediya.auth.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * An outbound port that defines the contract for user persistence operations.
 */
public interface UserRepository {

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check.
     * @return A reactive stream emitting true if the email exists, false otherwise.
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Persists a new User object.
     *
     * @param user The domain model object to save.
     * @return A reactive stream emitting the saved User, potentially with updated state from the database (like an ID).
     */
    Mono<User> save(User user);
}
