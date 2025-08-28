package com.crediya.auth.infrastructure.entrypoints.web.dto;

import com.crediya.auth.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for the user registration response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponse {

    private String email;
    private String message;

    /**
     * A static factory method to create a response from a domain User object.
     *
     * @param user The user domain object that was successfully saved.
     * @return A new UserRegistrationResponse object.
     */
    public static UserRegistrationResponse fromDomain(User user) {
        return UserRegistrationResponse.builder()
                .email(user.getEmail())
                .message("User registered successfully.")
                .build();
    }
}
