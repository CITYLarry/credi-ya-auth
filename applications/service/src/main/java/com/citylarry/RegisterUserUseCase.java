package com.citylarry;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase implements RegisterUserPort{

    private final UserRepository userRepository;

    @Override
    public Mono<User> registerUser(RegisterUserCommand command) {
        return userRepository.existsByEmail(command.email())
                .flatMap(emailExists -> {
                    if (Boolean.TRUE.equals(emailExists)) {
                        return Mono.error(new EmailAlreadyExistsException("Email " + command.email() + " is already registered."));
                    }
                    User userToRegister = command.toDomainUser();
                    return userRepository.save(userToRegister);
                });
    }
}
