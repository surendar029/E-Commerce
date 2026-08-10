package dev.project.productservice.dto;

import java.io.Serializable;

public record CategoryResponse(
        Long id,
        String name,
        String description
) {
}
