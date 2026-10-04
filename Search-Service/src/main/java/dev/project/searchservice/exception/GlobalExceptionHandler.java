package dev.project.searchservice.exception;

import dev.project.searchservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(e.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPriceRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPriceRange(
            InvalidPriceRangeException e, HttpServletRequest request) {

        return buildErrorResponse(e.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ":" + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(message, request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            HttpServletRequest request,
            HttpStatus status
    ) {

        ErrorResponse error = ErrorResponse.builder()
                .localDateTime(LocalDateTime.now())
                .status(status.value())
                .error(status.name())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}
