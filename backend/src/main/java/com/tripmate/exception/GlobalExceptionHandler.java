package com.tripmate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for TripMate Backend
 * 
 * This class provides centralized exception handling for the entire application.
 * It catches exceptions thrown by controllers and returns appropriate HTTP responses.
 * 
 * Key Concepts:
 * - @RestControllerAdvice: Marks this as a global exception handler
 * - @ExceptionHandler: Specifies which exceptions to handle
 * - ResponseEntity: Wraps error response with status code and body
 * - Consistent Error Format: All errors follow the same structure
 * - Logging: All exceptions are logged for debugging
 * 
 * Benefits:
 * - Consistent error responses across all endpoints
 * - Centralized error handling logic
 * - Better user experience with meaningful error messages
 * - Easier debugging with structured error logs
 * - Security: Don't expose internal error details to clients
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle validation errors (Bean Validation)
     * 
     * This method catches validation errors when @Valid annotation fails.
     * It extracts field-specific error messages and returns them in a structured format.
     * 
     * @param ex Validation exception
     * @param request Web request context
     * @return Error response with validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.error("Validation error: {}", ex.getMessage());
        
        // Extract field-specific validation errors
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Input validation failed")
            .path(request.getDescription(false))
            .fieldErrors(fieldErrors)
            .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle IllegalArgumentException (Business logic errors)
     * 
     * This method catches business logic exceptions thrown by service layer.
     * These are typically validation errors or business rule violations.
     * 
     * @param ex IllegalArgumentException
     * @param request Web request context
     * @return Error response with business error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.error("Business logic error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message(ex.getMessage())
            .path(request.getDescription(false))
            .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Handle ResourceNotFoundException (Custom exception for not found resources)
     * 
     * This method catches custom exceptions for resources that don't exist.
     * 
     * @param ex ResourceNotFoundException
     * @param request Web request context
     * @return Error response with 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false))
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle generic Exception (catch-all for unexpected errors)
     * 
     * This method catches any other exceptions that weren't handled by specific handlers.
     * It should be the last handler and should not expose internal error details.
     * 
     * @param ex Generic exception
     * @param request Web request context
     * @return Generic error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred. Please try again later.")
            .path(request.getDescription(false))
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle database constraint violations
     * 
     * This method catches database-related exceptions like duplicate key violations.
     * 
     * @param ex Database exception
     * @param request Web request context
     * @return Error response with appropriate status
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            org.springframework.dao.DataIntegrityViolationException ex, WebRequest request) {
        
        log.error("Database constraint violation: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Conflict")
            .message("The requested operation conflicts with existing data.")
            .path(request.getDescription(false))
            .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handle HTTP message conversion errors
     * 
     * This method catches errors when JSON cannot be parsed or converted.
     * 
     * @param ex HTTP message conversion exception
     * @param request Web request context
     * @return Error response with 400 status
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            org.springframework.http.converter.HttpMessageNotReadableException ex, WebRequest request) {
        
        log.error("HTTP message conversion error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message("Invalid request body format")
            .path(request.getDescription(false))
            .build();
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Error Response DTO
     * 
     * This class defines the structure of error responses returned by the API.
     * All error responses follow this consistent format.
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> fieldErrors;
        
        // Builder pattern for easy construction
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }
        
        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
        public Map<String, String> getFieldErrors() { return fieldErrors; }
        
        // Builder class
        public static class ErrorResponseBuilder {
            private ErrorResponse errorResponse = new ErrorResponse();
            
            public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
                errorResponse.timestamp = timestamp;
                return this;
            }
            
            public ErrorResponseBuilder status(int status) {
                errorResponse.status = status;
                return this;
            }
            
            public ErrorResponseBuilder error(String error) {
                errorResponse.error = error;
                return this;
            }
            
            public ErrorResponseBuilder message(String message) {
                errorResponse.message = message;
                return this;
            }
            
            public ErrorResponseBuilder path(String path) {
                errorResponse.path = path;
                return this;
            }
            
            public ErrorResponseBuilder fieldErrors(Map<String, String> fieldErrors) {
                errorResponse.fieldErrors = fieldErrors;
                return this;
            }
            
            public ErrorResponse build() {
                return errorResponse;
            }
        }
    }
    
    /**
     * Custom exception for resource not found scenarios
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
} 