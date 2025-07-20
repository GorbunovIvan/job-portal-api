package com.example.config;

import com.example.api.ApplicationRequestDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@ConditionalOnProperty(name = "spring.kafka.consumer.enabled", havingValue = "true")
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public JsonDeserializer<ApplicationRequestDTO> applicationRequestAndUserIdDTO() {
        JsonDeserializer<ApplicationRequestDTO> deserializer = new JsonDeserializer<>(ApplicationRequestDTO.class);
        deserializer.addTrustedPackages("*");
        return deserializer;
    }

    @Bean
    public Map<String, Object> consumerConfig() {

        var properties = new HashMap<String, Object>();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, applicationRequestAndUserIdDTO());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return properties;
    }

    @Bean
    public ConsumerFactory<String, ApplicationRequestDTO> consumerFactory(JsonDeserializer<ApplicationRequestDTO> ApplicationRequestAndUserIdDTO) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), ApplicationRequestAndUserIdDTO);
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(ConsumerFactory<String, ApplicationRequestDTO> consumerFactor) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ApplicationRequestDTO>();
        factory.setConsumerFactory(consumerFactor);
        return factory;
    }
}
