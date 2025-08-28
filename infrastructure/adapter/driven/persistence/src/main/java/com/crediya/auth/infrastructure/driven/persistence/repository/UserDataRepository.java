package com.crediya.auth.infrastructure.driven.persistence.repository;

import com.crediya.auth.infrastructure.driven.persistence.entity.UserData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository interface for the UserData entity.
 */
@Repository
public interface UserDataRepository extends R2dbcRepository<UserData, Long> {

    /**
     * A custom query method to check for the existence of a user by their email.
     *
     * @param email The email to check for.
     * @return A Mono<Boolean> emitting true if the email exists, false otherwise.
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Finds a user entity by its unique email address.
     *
     * @param email The email of the user to find.
     * @return A Mono emitting the found UserData, or empty if no user is found.
     */
    Mono<UserData> findByEmail(String email);
}
