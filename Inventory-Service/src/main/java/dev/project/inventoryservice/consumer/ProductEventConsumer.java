package dev.project.inventoryservice.consumer;

import dev.project.inventoryservice.event.ProductEvent;
import dev.project.inventoryservice.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductEventConsumer {

    private final InventoryService service;

    public ProductEventConsumer(InventoryService service) {
        this.service = service;
    }

    @KafkaListener(topics = "product-events")
    public void consume(ProductEvent event) {
        log.info("Received event from Kafka for Product ID: {}, Type: {}", event.id(), event.eventType());
        switch (event.eventType()) {
            case CREATED -> service.initializeStock(event.id(), event.stockQuantity());
            case UPDATED -> log.info("Product updated event received, no stock changes needed in Inventory.");
            case DELETED -> service.deleteStock(event.id());
        }
    }
}
