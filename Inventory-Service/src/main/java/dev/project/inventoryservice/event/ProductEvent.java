package dev.project.inventoryservice.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductEvent(
        Long id,
        Integer stockQuantity,
        EventType eventType
) {
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
