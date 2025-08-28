package com.crediya.auth.application.ports.in;

import reactor.core.publisher.Mono;

/**
 * Defines the contract for the use case of logging in a user.
 */
public interface LoginPort {

    /**
     * Executes the login process for a user.
     *
     * @param command The command object containing the user's credentials.
     * @return A reactive stream emitting the generated authentication token upon success,
     * or an error if the credentials are invalid or the user is not found.
     */
    Mono<String> login(LoginCommand command);
}