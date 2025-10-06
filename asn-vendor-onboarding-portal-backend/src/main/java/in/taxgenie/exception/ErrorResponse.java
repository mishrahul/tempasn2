package in.taxgenie.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response structure for all API errors
 * 
 * This class provides a consistent error response format across the application
 * including detailed error information, timestamps, and optional validation errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response structure")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "401")
    private int status;

    @Schema(description = "Error code for client-side handling", example = "TOKEN_EXPIRED")
    private String error;

    @Schema(description = "Human-readable error message", example = "JWT token has expired")
    private String message;

    @Schema(description = "Detailed error description", example = "Your session has expired. Please login again.")
    private String details;

    @Schema(description = "API path where error occurred", example = "/api/v1/oems/available")
    private String path;

    @Schema(description = "Timestamp when error occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    @Schema(description = "List of validation errors (for 400 Bad Request)")
    private List<ValidationError> validationErrors;

    /**
     * Validation error details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Validation error details")
    public static class ValidationError {
        
        @Schema(description = "Field name that failed validation", example = "email")
        private String field;
        
        @Schema(description = "Rejected value")
        private Object rejectedValue;
        
        @Schema(description = "Validation error message", example = "Email must be valid")
        private String message;
    }

    /**
     * Creates a simple error response
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with details
     */
    public static ErrorResponse of(int status, String error, String message, String details, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .details(details)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a validation error response
     */
    public static ErrorResponse ofValidation(String message, String path, List<ValidationError> validationErrors) {
        return ErrorResponse.builder()
                .status(400)
                .error("VALIDATION_ERROR")
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();
    }
}

