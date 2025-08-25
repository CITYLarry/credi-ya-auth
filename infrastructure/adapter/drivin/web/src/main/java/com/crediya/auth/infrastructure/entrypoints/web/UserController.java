package com.crediya.auth.infrastructure.entrypoints.web;

import com.crediya.auth.application.exceptions.EmailAlreadyExistsException;
import com.crediya.auth.application.ports.in.RegisterUserPort;
import com.crediya.auth.infrastructure.entrypoints.web.dto.ErrorResponse;
import com.crediya.auth.infrastructure.entrypoints.web.dto.UserRegistrationRequest;
import com.crediya.auth.infrastructure.entrypoints.web.dto.UserRegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user registration and management")
public class UserController {

    private final RegisterUserPort registerUserPort;

    /**
     * Handles the HTTP POST request to register a new user.
     *
     * @param request The request body containing the user's data, which is validated automatically.
     * @return A {@link Mono} emitting a {@link UserRegistrationResponse} upon successful creation.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user in the system based on the provided data."
    )
    @ApiResponse(responseCode = "201", description = "User created successfully.",
            content = @Content(schema = @Schema(implementation = UserRegistrationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Email already exists.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<UserRegistrationResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());
        return Mono.just(request)
                .map(UserRegistrationRequest::toCommand)
                .flatMap(registerUserPort::registerUser)
                .map(UserRegistrationResponse::fromDomain)
                .doOnSuccess(response -> log.info("Successfully registered user with email: {}", response.getEmail()));
    }

    /**
     * Exception handler that centralizes the logic for handling input validation errors for this controller.
     *
     * @param ex The captured {@link WebExchangeBindException} containing validation details.
     * @return A {@link Mono} emitting a standardized {@link ErrorResponse}.
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> handleValidationException(WebExchangeBindException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("'%s': %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        log.warn("Validation failed for registration request: {}", errors);
        return Mono.just(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors));
    }

    /**
     * Handles the business exception thrown when a user attempts to register with an email that already exists.
     *
     * @param ex The captured {@link EmailAlreadyExistsException}.
     * @return A {@link Mono} emitting a standardized {@link ErrorResponse} with a 409 Conflict status.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ErrorResponse> handleEmailExistsException(EmailAlreadyExistsException ex) {
        log.warn("Registration failed: {}", ex.getMessage());
        return Mono.just(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }
}
