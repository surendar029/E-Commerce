package dev.project.orderservice.dto;

public record InventoryDetails(
        Long productId,
        Integer availableQuantity
) {
}
