package com.crediya.auth.infrastructure.entrypoints.web.dto;

import com.crediya.auth.application.ports.in.LoginCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for the user login request.
 */
@Data
@Builder
public class LoginRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be in a valid format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    /**
     * Maps this web DTO to the application layer's command object.
     *
     * @return A {@link LoginCommand} object.
     */
    public LoginCommand toCommand() {
        return new LoginCommand(this.email, this.password);
    }
}
