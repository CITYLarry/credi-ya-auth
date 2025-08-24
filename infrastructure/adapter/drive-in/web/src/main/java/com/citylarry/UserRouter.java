package com.citylarry;

import com.citylarry.dto.ErrorResponse;
import com.citylarry.dto.UserRegistrationRequest;
import com.citylarry.dto.UserRegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Configuration class that defines the routes for the user-related endpoints.
 */
@Configuration
public class UserRouter {

    private static final String API_V1_USERS = "/api/v1/users";

    /**
     * Defines a RouterFunction bean that maps requests to handler methods.
     *
     * @param handler The UserHandler that contains the endpoint logic.
     * @return A RouterFunction that Spring will use to handle requests.
     */
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = API_V1_USERS,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,
                    beanMethod = "registerUser",
                    operation = @Operation(
                            summary = "Register a new user",
                            description = "Creates a new user in the system based on the provided data.",
                            tags = {"User Management"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "User data for registration.",
                                    content = @Content(schema = @Schema(implementation = UserRegistrationRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "User created successfully.",
                                            content = @Content(schema = @Schema(implementation = UserRegistrationResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input data.",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "409", description = "Email already exists.",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return RouterFunctions.route(
                POST(API_V1_USERS).and(accept(MediaType.APPLICATION_JSON)),
                handler::registerUser
        );
    }
}
