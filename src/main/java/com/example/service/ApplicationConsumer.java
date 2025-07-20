package com.example.service;

import com.example.api.ApplicationRequestAndUserIdDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.kafka.consumer.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class ApplicationConsumer {

    private final ApplicationService applicationService;

    @KafkaListener(topics = "${spring.kafka.topic-applications}")
    public void applicationMessageListener(ConsumerRecord<String, ApplicationRequestAndUserIdDTO> record) {

        log.info("Consumed record with application from Kafka: {}", record);

        ApplicationRequestAndUserIdDTO applicationAndUserId = record.value();
        var userId = applicationAndUserId.getUserId();
        var application = applicationAndUserId.getApplicationRequestDTO();

        try {
            applicationService.applyApplicantToJob(userId, application);
            log.info("Application created successfully: {}", applicationAndUserId);
        } catch (RuntimeException e) {
            log.error("Failed to persist the application: {}\n{}", applicationAndUserId, e.getMessage());
        }
    }
}
