package com.crediya.auth.application.usecase;

import com.crediya.auth.application.exceptions.InvalidCredentialsException;
import com.crediya.auth.application.ports.in.LoginCommand;
import com.crediya.auth.domain.model.Role;
import com.crediya.auth.domain.model.User;
import com.crediya.auth.domain.ports.out.PasswordEncoderPort;
import com.crediya.auth.domain.ports.out.TokenProviderPort;
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
 * Unit tests for the LoginUseCase.
 * @ExtendWith(MockitoExtension.class) enables Mockito annotations.
 */
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoderPort passwordEncoderPort;
    @Mock
    private TokenProviderPort tokenProviderPort;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {

        var command = new LoginCommand("larry.ramirez@outlook.com", "plainPassword123");
        var role = new Role(1L, "ROLE_CLIENT");
        var user = new User(
                1L,
                "Larry",
                "Ramirez",
                "larry.ramirez@outlook.com",
                "encodedPassword",
                "123456789",
                "3001234567",
                LocalDate.now(),
                "address",
                role,
                new BigDecimal("5000000")
        );
        var expectedToken = "dummy.jwt.token";

        when(userRepository.findByEmail(command.email())).thenReturn(Mono.just(user));
        when(passwordEncoderPort.matches(command.password(), user.getPassword())).thenReturn(true);
        when(tokenProviderPort.generateToken(user)).thenReturn(expectedToken);

        Mono<String> result = loginUseCase.login(command);

        StepVerifier.create(result)
                .expectNext(expectedToken)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenUserNotFound() {

        var command = new LoginCommand("notfound@example.com", "password123");
        when(userRepository.findByEmail(command.email())).thenReturn(Mono.empty());

        Mono<String> result = loginUseCase.login(command);

        StepVerifier.create(result)
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(passwordEncoderPort, never()).matches(any(), any());
        verify(tokenProviderPort, never()).generateToken(any());
    }

    @Test
    void shouldReturnErrorWhenPasswordIsIncorrect() {

        var command = new LoginCommand("larry.ramirez@outlook.com", "wrongPassword");
        var role = new Role(1L, "ROLE_CLIENT");
        var user = new User(1L, "Test", "User", "larry.ramirez@outlook.com", "encodedPassword",
                "123", "321", LocalDate.now(), "address", role, new BigDecimal("5000000"));

        when(userRepository.findByEmail(command.email())).thenReturn(Mono.just(user));
        when(passwordEncoderPort.matches(command.password(), user.getPassword())).thenReturn(false);

        Mono<String> result = loginUseCase.login(command);

        StepVerifier.create(result)
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(tokenProviderPort, never()).generateToken(any());
    }
}
