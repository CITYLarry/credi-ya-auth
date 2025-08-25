package com.crediya.auth.application.usecase;

import com.crediya.auth.application.exceptions.EmailAlreadyExistsException;
import com.crediya.auth.application.ports.in.RegisterUserCommand;
import com.crediya.auth.application.ports.in.RegisterUserPort;
import com.crediya.auth.domain.model.User;
import com.crediya.auth.domain.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase implements RegisterUserPort {

    private final UserRepository userRepository;

    /**
     * Orchestrates the registration of a new user.
     *
     * @param command The command object containing all necessary data for registration.
     * @return A reactive stream emitting the newly created User.
     */
    @Override
    @Transactional
    public Mono<User> registerUser(RegisterUserCommand command) {
        log.trace("Attempting to register user with email: {}", command.email());

        return userRepository.existsByEmail(command.email())
                .flatMap(emailExists -> {
                    if (Boolean.TRUE.equals(emailExists)) {
                        log.warn("Registration failed: Email {} already exists.", command.email());
                        return Mono.error(new EmailAlreadyExistsException("Email " + command.email() + " is already registered."));
                    }
                    log.trace("Email {} is available. Proceeding with user creation.", command.email());
                    User userToRegister = command.toDomainUser();
                    return userRepository.save(userToRegister)
                            .doOnSuccess(savedUser -> log.trace("Successfully saved user with ID: {}", savedUser.getId()));
                });
    }
}
