package com.creditya.auth.application.ports.in;

import com.crediya.auth.domain.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a command to register a new user.
 */
public record RegisterUserCommand(
        String firstName,
        String lastName,
        String email,
        String identityNumber,
        String phoneNumber,
        LocalDate birthDate,
        String address,
        String idRole,
        BigDecimal baseSalary
) {
    public RegisterUserCommand {
        Objects.requireNonNull(firstName, "First name must not be null");
        Objects.requireNonNull(lastName, "Last name must not be null");
        Objects.requireNonNull(email, "Email must not be null");
        Objects.requireNonNull(baseSalary, "Base salary must not be null");
    }

    /**
     * A factory method to convert this command object into a domain User object.
     *
     * @return A new User domain object.
     */
    public User toDomainUser() {
        return User.newUser(
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
