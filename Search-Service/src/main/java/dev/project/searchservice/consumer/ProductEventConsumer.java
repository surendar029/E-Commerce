package dev.project.searchservice.consumer;

import dev.project.searchservice.event.ProductEvent;
import dev.project.searchservice.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ProductEventConsumer {

    private final SearchService searchService;

    public ProductEventConsumer(SearchService searchService) {
        this.searchService = searchService;
    }

    @KafkaListener(topics = "product-events")
    public void consume(ProductEvent event) {
        log.info("Received event from Kafka for Product ID: {}, Type: {}", event.id(), event.eventType());

        switch (event.eventType()) {
            case CREATED, UPDATED -> {
                log.info("Indexing product in Elasticsearch: {}", event.id());
                searchService.indexProduct(event);
            }
            case DELETED -> {
                log.info("Deleting product from Elasticsearch: {}", event.id());
                searchService.deleteProduct(event.id());
            }
        }
    }
}
