package com.crediya.auth.infrastructure.entrypoints.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for the user login response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * A static factory method to create a response from a token string.
     *
     * @param token The generated JWT.
     * @return A new LoginResponse object.
     */
    public static LoginResponse fromToken(String token) {
        return new LoginResponse(token, "Bearer");
    }
}
