package com.crediya.auth.application.usecase;

import com.crediya.auth.application.exceptions.EmailAlreadyExistsException;
import com.crediya.auth.application.ports.in.RegisterUserCommand;
import com.crediya.auth.application.usecase.RegisterUserUseCase;
import com.crediya.auth.domain.model.User;
import com.crediya.auth.domain.ports.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the RegisterUserUseCase.
 * @ExtendWith(MockitoExtension.class) enables Mockito annotations.
 */
@ExtendWith(MockitoExtension.class)
public class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void shouldRegisterUserSuccessfullyWhenEmailDoesNotExist() {

        var command = new RegisterUserCommand(
                "Larry", "Ramirez", "larry.ramirez11@outlook.com", "123456789",
                "3001234567",
                LocalDate.of(1990, 5, 15),
                "123 Main St",
                "ROLE_USER", new BigDecimal("5000000")
        );
        User userToSave = command.toDomainUser();

        when(userRepository.existsByEmail(command.email())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userToSave));

        Mono<User> result = registerUserUseCase.registerUser(command);

        StepVerifier.create(result)
                .expectNextMatches(
                        savedUser ->
                            savedUser.getFirstName().equals("Larry") &&
                            savedUser.getLastName().equals("Ramirez") &&
                            savedUser.getEmail().equals("larry.ramirez11@outlook.com") &&
                            savedUser.getIdentityNumber().equals("123456789") &&
                            savedUser.getPhoneNumber().equals("3001234567") &&
                            savedUser.getBirthDate().equals(LocalDate.of(1990, 5, 15)) &&
                            savedUser.getAddress().equals("123 Main St") &&
                            savedUser.getIdRole().equals("ROLE_USER") &&
                            savedUser.getBaseSalary().equals(new BigDecimal("5000000"))
                )
                .verifyComplete();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturnErrorWhenEmailAlreadyExists() {

        var command = new RegisterUserCommand(
                "Larry", "Ramirez", "larry.ramirez11@outlook.com", "123456789",
                "3001234567",
                LocalDate.of(1990, 5, 15),
                "123 Main St",
                "ROLE_USER", new BigDecimal("5000000")
        );

        when(userRepository.existsByEmail(command.email())).thenReturn(Mono.just(true));

        Mono<User> result = registerUserUseCase.registerUser(command);


        StepVerifier.create(result)
                .expectError(EmailAlreadyExistsException.class)
                .verify();

        verify(userRepository, never()).save(any(User.class));
    }
}
