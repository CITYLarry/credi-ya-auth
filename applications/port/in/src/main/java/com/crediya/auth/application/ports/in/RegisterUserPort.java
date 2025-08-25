package com.crediya.auth.application.ports.in;

import com.crediya.auth.domain.model.User;
import reactor.core.publisher.Mono;

/**
 * Defines the contract for the use case of registering a new user.
 */
public interface RegisterUserPort {

    /**
     * Orchestrates the registration of a new user.
     *
     * @param command The command object containing all necessary data for registration.
     * @return A reactive stream emitting the newly created User.
     */
    Mono<User> registerUser(RegisterUserCommand command);
}
