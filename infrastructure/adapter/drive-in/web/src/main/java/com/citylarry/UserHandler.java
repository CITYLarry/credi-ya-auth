package com.citylarry;

import com.citylarry.dto.ErrorResponse;
import com.citylarry.dto.UserRegistrationRequest;
import com.citylarry.dto.UserRegistrationResponse;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * Handles the functional web endpoints for user registration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {

    private final RegisterUserPort registerUserPort;
    private final Validator validator;

    /**
     * Handles the user registration request.
     *
     * @param request The incoming ServerRequest.
     * @return A Mono<ServerResponse> indicating the result of the operation.
     */
    public Mono<ServerResponse> registerUser(ServerRequest request) {
        return request.bodyToMono(UserRegistrationRequest.class)
                .doOnNext(this::validateRequest)
                .map(UserRegistrationRequest::toCommand)
                .flatMap(registerUserPort::registerUser)
                .flatMap(domainUser -> {
                    var responseDto = UserRegistrationResponse.fromDomain(domainUser);
                    log.info("Successfully registered user with email: {}", responseDto.getEmail());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(responseDto);
                })
                .onErrorResume(EmailAlreadyExistsException.class, this::handleEmailExistsError)
                .onErrorResume(ServerWebInputException.class, this::handleValidationError);
    }

    /**
     * Programmatically validates the request DTO.
     * @param request The DTO to validate.
     */
    private void validateRequest(UserRegistrationRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            log.warn("Validation failed for registration request: {}", errors);

            throw new ServerWebInputException(errors);
        }
    }

    private Mono<ServerResponse> handleEmailExistsError(EmailAlreadyExistsException ex) {
        log.warn("Registration failed: {}", ex.getMessage());
        return ServerResponse.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    private Mono<ServerResponse> handleValidationError(ServerWebInputException ex) {
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getReason()));
    }
}
