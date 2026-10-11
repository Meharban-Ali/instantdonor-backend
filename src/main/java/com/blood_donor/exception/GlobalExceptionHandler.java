package com.blood_donor.exception;

import com.blood_donor.model.dto.response.api_response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;  // ✅ FIX 1
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom API exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException apiException) {
        log.warn("API Exception: {}", apiException.getMessage());
        return ResponseEntity
                .status(apiException.getStatus())
                .body(ApiResponse.failure(apiException.getMessage()));
    }

    // Validation errors — @Valid failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidateErrors(
            MethodArgumentNotValidException exp) {
        Map<String, String> errors = exp.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Validation Failed: " + errors));
    }

    // Wrong credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentials(
            BadCredentialsException exp) {
        log.warn("Bad credentials attempt: {}", exp.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure("Invalid email or password"));
    }

    // Access denied — wrong role
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(
            AccessDeniedException exp) {
        log.warn("Access denied: {}", exp.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure("Access Denied: insufficient permission"));
    }

    // Catch-all
    @ExceptionHandler(Exception.class) // ✅ FIX 2
    public ResponseEntity<ApiResponse<?>> handleGenericsException(Exception exp) {
        log.error("Unexpected Error: ", exp); // ✅ FIX 3
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Something went wrong, please try again"));
    }
}