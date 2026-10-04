package dev.project.searchservice.dto;

import java.math.BigDecimal;

public record ProductSearchResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String category
) { }
