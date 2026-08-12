package dev.project.searchservice.service;

import dev.project.searchservice.document.ProductDocument;
import dev.project.searchservice.event.ProductEvent;
import dev.project.searchservice.repository.ProductSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductEventConsumer {

    private final ProductSearchRepository searchRepository;
    private final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    public ProductEventConsumer(ProductSearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @KafkaListener(topics = "product-events", groupId = "search-service-group")
    public void consumeProductEvent(ProductEvent event) {
        log.info("Received ProductEvent for Product ID: {}, Type: {}", event.id(), event.eventType());
        switch (event.eventType()){
            case CREATED,UPDATED -> {
                ProductDocument document=mapToDocument(event);
                searchRepository.save(document);
                log.info("Indexed product in Elasticsearch with ID: {}", event.id());
            }
            case DELETED -> {
                ProductDocument document=mapToDocument(event);
                searchRepository.delete(document);
                log.info("Deleted product from Elasticsearch with ID: {}", event.id());
            }
        }
    }


    public ProductDocument mapToDocument(ProductEvent event){
        return new ProductDocument(
                event.id().toString(),
                event.name(),
                event.description(),
                event.price(),
                event.stockQuantity(),
                event.categoryId(),
                event.categoryName()
        );
    }
}
