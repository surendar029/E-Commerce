package dev.project.orderservice.dto;

import java.math.BigDecimal;

public record ProductDetails(
        Long id,
        String name,
        BigDecimal price,
        String categoryName
) {
}
