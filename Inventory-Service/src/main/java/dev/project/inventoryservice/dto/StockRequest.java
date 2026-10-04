package dev.project.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockRequest(
        @NotNull(message = "Product Id is required")
        Long productId,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 1, message = "Stock quantity must be greater than 0")
        Integer quantity
) { }
