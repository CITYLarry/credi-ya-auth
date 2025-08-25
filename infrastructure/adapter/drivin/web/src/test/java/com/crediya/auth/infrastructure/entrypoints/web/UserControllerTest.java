package com.crediya.auth.infrastructure.entrypoints.web;

import com.crediya.auth.application.exceptions.EmailAlreadyExistsException;
import com.crediya.auth.application.ports.in.RegisterUserPort;
import com.crediya.auth.domain.model.User;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

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

        User registeredUser = User.newUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getIdentityNumber(),
                request.getPhoneNumber(),
                request.getBirthDate(),
                request.getAddress(),
                request.getIdRole(),
                request.getBaseSalary()
        );

        when(registerUserPort.registerUser(any())).thenReturn(Mono.just(registeredUser));

        webTestClient.post().uri("/api/v1/users")
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

        webTestClient.post().uri("/api/v1/users")
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
                .baseSalary(new BigDecimal("5000000"))
                .birthDate(LocalDate.of(1995, 11, 11))
                .address("123 Main St")
                .build();


        when(registerUserPort.registerUser(any()))
                .thenReturn(Mono.error(new EmailAlreadyExistsException("Email " + request.getEmail() + " is already registered.")));


        webTestClient.post().uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.message").isEqualTo("Email " + request.getEmail() + " is already registered.");
    }
}
