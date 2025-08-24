package com.citylarry;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public class User {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE
    );
    private static final BigDecimal MINIMUM_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAXIMUM_SALARY = new BigDecimal("15000000");


    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String identityNumber;
    private final String phoneNumber;
    private final String idRole;
    private final BigDecimal baseSalary;


    public User(
            Long id,
            String firstName,
            String lastName,
            String email,
            String identityNumber,
            String phoneNumber,
            String idRole,
            BigDecimal baseSalary
            ) {

        validateFieldNotNullOrEmpty(firstName, "First name cannot be null or empty.");
        validateFieldNotNullOrEmpty(lastName, "Last name cannot be null or empty.");
        validateFieldNotNullOrEmpty(email, "Email cannot be null or empty.");
        validateEmailFormat(email);
        validateSalary(baseSalary);

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.identityNumber = identityNumber;
        this.phoneNumber = phoneNumber;
        this.idRole = idRole;
        this.baseSalary = baseSalary;
    }

    public static User newUser(String firstName, String lastName, String email, String identityNumber, String phoneNumber, String idRole, BigDecimal baseSalary) {
        return new User(null, firstName, lastName, email, identityNumber, phoneNumber, idRole, baseSalary);
    }

    private void validateFieldNotNullOrEmpty(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("The email format is not valid.");
        }
    }

    private void validateSalary(BigDecimal salary) {
        Objects.requireNonNull(salary, "Base salary cannot be null.");
        if (salary.compareTo(MINIMUM_SALARY) < 0 || salary.compareTo(MAXIMUM_SALARY) > 0) {
            throw new IllegalArgumentException("Base salary must be between " + MINIMUM_SALARY + " and " + MAXIMUM_SALARY + ".");
        }
    }


    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getIdRole() {
        return idRole;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
}
