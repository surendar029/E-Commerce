package dev.project.productservice.producer;

import dev.project.productservice.event.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private static final String TOPIC = "product-events";

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public void publishEvent(ProductEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.id().toString(),
                event
        ).whenComplete((result, ex) -> {

            if (ex != null) {
                log.error(
                        "Failed to publish product event for productId={}",
                        event.id(),
                        ex
                );
                return;
            }

            log.info(
                    "Product event published: productId={}, eventType={}, partition={}, offset={}",
                    event.id(),
                    event.eventType(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );
        });
    }
}