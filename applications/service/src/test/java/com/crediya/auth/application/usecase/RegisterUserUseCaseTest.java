package com.crediya.auth.application.usecase;

import com.crediya.auth.application.exceptions.EmailAlreadyExistsException;
import com.crediya.auth.application.exceptions.RoleNotFoundException;
import com.crediya.auth.application.ports.in.RegisterUserCommand;
import com.crediya.auth.domain.model.Role;
import com.crediya.auth.domain.model.User;
import com.crediya.auth.domain.ports.out.PasswordEncoderPort;
import com.crediya.auth.domain.ports.out.RoleRepository;
import com.crediya.auth.domain.ports.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoderPort passwordEncoderPort; // FIX: Mock para la nueva dependencia
    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor; // FIX: Captor para verificar el usuario guardado

    @Test
    void shouldRegisterUserSuccessfullyWhenEmailDoesNotExistAndRoleExists() {

        var roleName = "ROLE_CLIENT";
        var plainPassword = "password123";
        var hashedPassword = "hashedPassword123";
        var command = new RegisterUserCommand(
                "Larry",
                "Ramirez",
                "larry.ramirez11@outlook.com",
                plainPassword,
                "123456789",
                "3001234567",
                LocalDate.of(1995, 11, 11),
                "123 Main St",
                roleName, new BigDecimal("5000000")
        );
        var role = new Role(1L, roleName);
        var userToSave = new User(
                1L,
                command.firstName(),
                command.lastName(),
                command.email(),
                hashedPassword,
                command.identityNumber(),
                command.phoneNumber(),
                command.birthDate(),
                command.address(),
                role,
                command.baseSalary());

        when(userRepository.existsByEmail(command.email())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(roleName)).thenReturn(Mono.just(role));
        when(passwordEncoderPort.encode(plainPassword)).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userToSave));

        Mono<User> result = registerUserUseCase.registerUser(command);

        StepVerifier.create(result).expectNextMatches(savedUser -> savedUser.getId() != null && savedUser.getEmail().equals(command.email())).verifyComplete();

        verify(userRepository).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor.getValue().getPassword()).isEqualTo(hashedPassword);
    }

    @Test
    void shouldReturnErrorWhenEmailAlreadyExists() {

        var command = new RegisterUserCommand("Larry", "Ramirez", "larry.ramirez11@outlook.com",
                "password123", "123456789", "3001234567",
                LocalDate.of(1995, 11, 11), "123 Main St", "ROLE_CLIENT", new BigDecimal("5000000"));
        when(userRepository.existsByEmail(command.email())).thenReturn(Mono.just(true));

        Mono<User> result = registerUserUseCase.registerUser(command);

        StepVerifier.create(result).expectError(EmailAlreadyExistsException.class).verify();
        verify(passwordEncoderPort, never()).encode(any()); // Verificar que no se intenta hashear
    }

    @Test
    void shouldReturnErrorWhenRoleIsNotFound() {

        var roleName = "NON_EXISTENT_ROLE";
        var command = new RegisterUserCommand(
                "Larry",
                "Ramirez",
                "larry.ramirez11@outlook.com",
                "password123",
                "123456789",
                "3001234567",
                LocalDate.of(1990, 5, 15),
                "123 Main St",
                roleName, new BigDecimal("5000000")
        );
        when(userRepository.existsByEmail(command.email())).thenReturn(Mono.just(false));
        when(roleRepository.findByName(roleName)).thenReturn(Mono.empty());

        Mono<User> result = registerUserUseCase.registerUser(command);

        StepVerifier.create(result).expectError(RoleNotFoundException.class).verify();
        verify(passwordEncoderPort, never()).encode(any());
    }
}
