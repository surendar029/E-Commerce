package dev.project.userservice.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String errorMessage,
        String message,
        String path
) {
}
