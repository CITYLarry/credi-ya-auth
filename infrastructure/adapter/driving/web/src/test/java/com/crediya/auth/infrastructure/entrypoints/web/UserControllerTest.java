package com.crediya.auth.infrastructure.entrypoints.web;

import com.crediya.auth.application.exceptions.EmailAlreadyExistsException;
import com.crediya.auth.application.exceptions.InvalidCredentialsException;
import com.crediya.auth.application.ports.in.LoginPort;
import com.crediya.auth.application.ports.in.RegisterUserPort;
import com.crediya.auth.domain.model.Role;
import com.crediya.auth.domain.model.User;
import com.crediya.auth.infrastructure.entrypoints.web.dto.LoginRequest;
import com.crediya.auth.infrastructure.entrypoints.web.dto.LoginResponse;
import com.crediya.auth.infrastructure.entrypoints.web.dto.UserRegistrationRequest;
import com.crediya.auth.infrastructure.entrypoints.web.dto.UserRegistrationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the UserController.
 *
 * It uses @WebFluxTest to test the web layer in isolation, mocking the use case port.
 */
@WebFluxTest(UserController.class)
class UserControllerTest {

    @SpringBootApplication
    @ComponentScan(basePackages = "com.crediya.auth.infrastructure.entrypoints.web")
    static class TestConfiguration {
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegisterUserPort registerUserPort;

    @MockBean
    private LoginPort loginPort;

    @Test
    void shouldReturnCreatedWhenUserIsRegisteredSuccessfully() {

        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("Larry")
                .lastName("Ramirez")
                .email("larry.ramirez11@outlook.com")
                .baseSalary(new BigDecimal("5000000"))
                .birthDate(LocalDate.of(1995, 11, 11))
                .address("123 Main St")
                .identityNumber("123456789")
                .phoneNumber("3001234567")
                .idRole("APPLICANT")
                .build();

        var role = new Role(1L, "ROLE_CLIENT");
        var registeredUser = new User(
                1L,
                "Larry",
                "Ramirez",
                "larry.ramirez11@outlook.com",
                "hashedPassword",
                "123456789",
                "3001234567",
                LocalDate.of(1995, 11, 11),
                "123 Main St",
                role,
                new BigDecimal("5000000")
        );


        when(registerUserPort.registerUser(any())).thenReturn(Mono.just(registeredUser));

        webTestClient.post().uri("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserRegistrationResponse.class)
                .value(response -> {
                    assert response.getEmail().equals(request.getEmail());
                    assert response.getMessage().equals("User registered successfully.");
                });
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() {

        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("")
                .lastName("User")
                .email("not-an-email")
                .address("")
                .baseSalary(new BigDecimal("-100"))
                .build();

        webTestClient.post().uri("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() {

        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .firstName("Larry")
                .lastName("Ramirez")
                .email("larry.ramirez11@outlook.com")
                .password("SecurePassword123")
                .baseSalary(new BigDecimal("5000000"))
                .birthDate(LocalDate.of(1995, 11, 11))
                .address("123 Main St")
                .build();


        when(registerUserPort.registerUser(any()))
                .thenReturn(Mono.error(new EmailAlreadyExistsException("Email " + request.getEmail() + " is already registered.")));


        webTestClient.post().uri("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.message").isEqualTo("Email " + request.getEmail() + " is already registered.");
    }

    @Test
    void shouldReturnOkAndTokenWhenLoginIsSuccessful() {

        var request = LoginRequest.builder()
                .email("larry.ramirez11@outlook.com")
                .password("password123")
                .build();
        var token = "generated.jwt.token";
        when(loginPort.login(any())).thenReturn(Mono.just(token));

        webTestClient.post().uri("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .value(response -> {
                    assertThat(response.getTokenType()).isEqualTo("Bearer");
                    assertThat(response.getToken()).isEqualTo(token);
                });
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() {

        var request = LoginRequest.builder()
                .email("larry.ramirez11@outlook.com")
                .password("wrong-password")
                .build();
        when(loginPort.login(any())).thenReturn(Mono.error(new InvalidCredentialsException("Invalid email or password.")));

        webTestClient.post().uri("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.message").isEqualTo("Invalid email or password.");
    }

    @Test
    void shouldReturnBadRequestWhenLoginRequestIsInvalid() {

        var request = LoginRequest.builder()
                .email("not-an-email")
                .password("")
                .build();

        webTestClient.post().uri("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
