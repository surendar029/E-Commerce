package dev.project.inventoryservice.dto;

import lombok.Builder;

@Builder
public record InventoryResponse(
        Long productId,
        Integer availableQuantity,
        Integer reservedQuantity
) {}
