package dev.project.searchservice.config;

import dev.project.searchservice.event.ProductEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;


import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private static final String PRODUCT_EVENTS_DLT_TOPIC = "product-events.DLT";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offsetReset;

    @Bean
    public NewTopic productEventsDltTopic() {
        return TopicBuilder.name(PRODUCT_EVENTS_DLT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public ConsumerFactory<String, ProductEvent> productEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,  groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        JacksonJsonDeserializer<ProductEvent> jsonDeserializer =
                new JacksonJsonDeserializer<>(ProductEvent.class);

        ErrorHandlingDeserializer<ProductEvent> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, ProductEvent> productEventConsumerFactory,
            KafkaTemplate<Object, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, ProductEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productEventConsumerFactory);

        DeadLetterPublishingRecoverer recoverer=new DeadLetterPublishingRecoverer(kafkaTemplate);
        DefaultErrorHandler handler=new DefaultErrorHandler(recoverer,new FixedBackOff(1000L,3L));
        factory.setCommonErrorHandler(handler);

        return factory;
    }
}
