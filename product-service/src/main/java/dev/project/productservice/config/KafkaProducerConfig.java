package dev.project.productservice.config;


import com.fasterxml.jackson.databind.JsonDeserializer;
import dev.project.productservice.event.ProductEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public static final String PRODUCT_EVENTS_TOPIC = "product-events";

    @Bean
    public NewTopic productEventsTopic(){
        return TopicBuilder.name(PRODUCT_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, ProductEvent> productEventProducerFactory(){
        Map<String,Object> props=new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        props.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS,false);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new JacksonJsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, ProductEvent> productEventKafkaTemplate(
            ProducerFactory<String, ProductEvent> productEventProducerFactory) {
        return new KafkaTemplate<>(productEventProducerFactory);
    }

}
