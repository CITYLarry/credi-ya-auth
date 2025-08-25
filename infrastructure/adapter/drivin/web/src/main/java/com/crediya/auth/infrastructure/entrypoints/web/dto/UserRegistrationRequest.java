package com.crediya.auth.infrastructure.entrypoints.web.dto;

import com.crediya.auth.application.ports.in.RegisterUserCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for the user registration request.
 */
@Data
@Builder
public class UserRegistrationRequest {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    private String identityNumber;
    private String phoneNumber;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private String address;
    private String idRole;

    @NotNull(message = "Base salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Salary cannot be negative")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "Salary exceeds maximum limit")
    private BigDecimal baseSalary;

    /**
     * Maps this DTO to the application layer's RegisterUserCommand.
     *
     * @return A RegisterUserCommand object.
     */
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(
                this.firstName,
                this.lastName,
                this.email,
                this.identityNumber,
                this.phoneNumber,
                this.birthDate,
                this.address,
                this.idRole,
                this.baseSalary
        );
    }
}
