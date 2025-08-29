package com.crediya.auth.application.usecase;

import com.crediya.auth.application.exceptions.EmailAlreadyExistsException;
import com.crediya.auth.application.exceptions.RoleNotFoundException;
import com.crediya.auth.application.ports.in.RegisterUserCommand;
import com.crediya.auth.application.ports.in.RegisterUserPort;
import com.crediya.auth.domain.model.User;
import com.crediya.auth.domain.ports.out.PasswordEncoderPort;
import com.crediya.auth.domain.ports.out.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    /**
     * Orchestrates the registration of a new user.
     *
     * @param command The command object containing all necessary data for registration.
     * @return A reactive stream emitting the newly created User.
     */
    @Override
    @Transactional
    public Mono<User> registerUser(RegisterUserCommand command) {
        log.info("Attempting to register user with email: {}", command.email());
        return userRepository.existsByEmail(command.email())
                .flatMap(emailExists -> {
                    if (Boolean.TRUE.equals(emailExists)) {
                        log.warn("Registration failed: Email {} already exists.", command.email());
                        return Mono.error(new EmailAlreadyExistsException("Email " + command.email() + " is already registered."));
                    }
                    log.debug("Email {} is available. Searching for role: {}", command.email(), command.roleName());
                    return roleRepository.findByName(command.roleName())
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("Registration failed: Role '{}' not found for email {}.", command.roleName(), command.email());
                                return Mono.error(new RoleNotFoundException("Role '" + command.roleName() + "' does not exist."));
                            }))
                            .flatMap(role -> {
                                log.debug("Role '{}' found. Proceeding with user creation.", command.roleName());

                                String encodedPassword = passwordEncoderPort.encode(command.password());

                                User userToRegister = new User(null,
                                        command.firstName(),
                                        command.lastName(),
                                        command.email(),
                                        encodedPassword,
                                        command.identityNumber(),
                                        command.phoneNumber(),
                                        command.birthDate(),
                                        command.address(), role,
                                        command.baseSalary()
                                );

                                return userRepository.save(userToRegister)
                                        .doOnSuccess(savedUser -> log.info("Successfully saved user with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail()));
                            });
                });
    }
}
