package dev.project.inventoryservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        LocalDateTime localDateTime,
        int status,
        String error,
        String message,
        String path
) {
}
