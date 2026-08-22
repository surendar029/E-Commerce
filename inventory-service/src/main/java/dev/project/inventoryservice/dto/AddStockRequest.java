package dev.project.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddStockRequest(
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 0,message = "Quantity must be greater than 1")
        Integer quantity
) { }
