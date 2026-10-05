package dev.project.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,

        String description,

        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        Long categoryId
) {
}
