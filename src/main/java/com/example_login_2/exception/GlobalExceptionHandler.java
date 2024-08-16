package com.example_login_2.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(HttpStatus.BAD_REQUEST.value())
                .setError(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .setMessage(errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(HttpStatus.BAD_REQUEST.value())
                .setError(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(HttpStatus.UNAUTHORIZED.value())
                .setError(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(HttpStatus.FORBIDDEN.value())
                .setError(HttpStatus.FORBIDDEN.getReasonPhrase())
                .setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(HttpStatus.NOT_FOUND.value())
                .setError(HttpStatus.NOT_FOUND.getReasonPhrase())
                .setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(HttpStatus.CONFLICT.value())
                .setError(HttpStatus.CONFLICT.getReasonPhrase())
                .setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GoneException.class)
    public ResponseEntity<ErrorResponse> handleGoneException(GoneException ex) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(HttpStatus.GONE.value())
                .setError(HttpStatus.GONE.getReasonPhrase())
                .setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException ex) {
        ErrorResponse errorResponse = new ErrorResponse()
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class ErrorResponse {
        private LocalDateTime timestamp = LocalDateTime.now();

        private int status;

        private String error;

        private String message;
    }
}
