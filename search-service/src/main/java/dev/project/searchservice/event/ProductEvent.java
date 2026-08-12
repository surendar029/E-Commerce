package dev.project.searchservice.event;

import java.math.BigDecimal;

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
