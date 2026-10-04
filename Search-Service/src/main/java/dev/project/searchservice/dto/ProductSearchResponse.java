package dev.project.searchservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductSearchResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String category
) { }
