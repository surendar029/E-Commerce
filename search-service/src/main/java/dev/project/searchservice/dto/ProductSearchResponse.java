package dev.project.searchservice.dto;

import java.math.BigDecimal;

public record ProductSearchResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Long categoryId,
        String categoryName
) { }
