package dev.project.searchservice.consumer;

import dev.project.searchservice.event.ProductEvent;
import dev.project.searchservice.mapper.ProductMapper;
import dev.project.searchservice.repository.ProductSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConsumer {

    private final ProductSearchRepository searchRepository;
    private final ProductMapper productMapper;
    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    public ProductEventConsumer(ProductSearchRepository searchRepository, ProductMapper productMapper) {
        this.searchRepository = searchRepository;
        this.productMapper = productMapper;
    }

    @KafkaListener(topics = "product-events", groupId = "search-service-group")
    public void consume(ProductEvent event) {
        if (event == null || event.id() == null) {
            log.warn("Received invalid or null product event, skipping...");
            return;
        }
        switch (event.eventType()) {
            case CREATED, UPDATED -> {
                log.info("Event Successfully: {}", event.eventType());
                searchRepository.save(productMapper.toDocument(event));
            }

            case DELETED -> {
                log.info("Event Successfully: {}", event.eventType());
                searchRepository.deleteById(event.id().toString());
            }

            default -> log.warn("Unhandled event type: {}", event.eventType());
        }
    }

}

