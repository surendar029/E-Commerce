package dev.project.searchservice.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
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
