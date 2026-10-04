package dev.project.productservice.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime localDateTime,
        int status,
        String errorMessage,
        String message,
        String path
) { }
