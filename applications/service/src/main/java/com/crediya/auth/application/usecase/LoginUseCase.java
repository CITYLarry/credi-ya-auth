package com.crediya.auth.application.usecase;

import com.crediya.auth.application.exceptions.InvalidCredentialsException;
import com.crediya.auth.application.ports.in.LoginCommand;
import com.crediya.auth.application.ports.in.LoginPort;
import com.crediya.auth.domain.ports.out.PasswordEncoderPort;
import com.crediya.auth.domain.ports.out.TokenProviderPort;
import com.crediya.auth.domain.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implements the use case for user login.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUseCase implements LoginPort {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    /**
     * Executes the login process by validating user credentials and generating a token upon success.
     *
     * @param command The command object containing the user's credentials.
     * @return A Mono emitting the JWT, or an InvalidCredentialsException error.
     */
    @Override
    public Mono<String> login(LoginCommand command) {
        log.info("Login attempt for email: {}", command.email());
        return userRepository.findByEmail(command.email())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Login failed: User not found for email {}", command.email());
                    return Mono.error(new InvalidCredentialsException("Invalid email or password."));
                }))
                .flatMap(user -> {
                    if (passwordEncoderPort.matches(command.password(), user.getPassword())) {
                        log.info("Password matched for user {}. Generating token.", user.getEmail());
                        String token = tokenProviderPort.generateToken(user);
                        return Mono.just(token);
                    } else {
                        log.warn("Login failed: Invalid password for user {}", user.getEmail());
                        return Mono.error(new InvalidCredentialsException("Invalid email or password."));
                    }
                });
    }
}
