package dev.project.productservice.event;


import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductEvent(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Long categoryId,
        String categoryName,
        EventType eventType
) {
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}