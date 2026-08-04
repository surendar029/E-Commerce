package dev.project.userservice.dto;

public record AuthResponse(
        String token,
        String tokenType
) {
    public AuthResponse(String token) {
        this(token, "Bearer");
    }
}
