package com.citylarry.dto;

/**
 * A generic DTO for standardized error responses across the API.
 *
 * @param status  The HTTP status code.
 * @param message A descriptive error message.
 */
public record ErrorResponse(int status, String message) {
}
