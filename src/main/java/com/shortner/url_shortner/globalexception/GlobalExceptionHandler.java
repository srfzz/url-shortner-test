package com.shortner.url_shortner.globalexception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.shortner.url_shortner.apiresponse.ApiReponse;
import com.shortner.url_shortner.exceptions.ResourceNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiReponse<Object>> handleAllExceptions(Exception ex) {
        return buildErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiReponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiReponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiReponse<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing));

        return buildErrorResponse("Validation failed", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiReponse<Object>> handleAuthenticationException(AuthenticationException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, null);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiReponse<Object>> handleJwtException(JwtException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, null);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiReponse<Object>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiReponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException e) {

        String message = e.getMostSpecificCause().getMessage();

        Map<String, String> errors = new HashMap<>();

        if (message != null && message.contains("Key")) {
            try {
                String field = message.substring(
                        message.indexOf("(") + 1,
                        message.indexOf(")"));

                errors.put(field, field + " already exists");

            } catch (Exception ignored) {
                errors.put("database", "Duplicate value violation");
            }
        }

        return buildErrorResponse("Validation failed", HttpStatus.CONFLICT, errors);
    }

    private ResponseEntity<ApiReponse<Object>>

            buildErrorResponse(String message, HttpStatus status, Object data) {

        ApiReponse<Object> response = ApiReponse.builder()
                .success(false)
                .statusCode(status.value())
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }

}
