package dev.project.inventoryservice.dto;

public record InventoryResponse(
        Long productId,
        Integer availableQuantity,
        Integer reservedQuantity,
        boolean inStock
) { }
