package com.example.service;

import com.example.api.ApplicationRequestAndUserIdDTO;
import com.example.api.ApplicationRequestDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class ApplicationConsumerTest {

    private ApplicationConsumer applicationConsumer;

    private ApplicationService applicationService;

    private final String TOPIC_NAME = "topic-applications-test";

    private ApplicationRequestAndUserIdDTO requestDTO;

    @BeforeEach
    void setUp() {

        applicationService = Mockito.mock();
        applicationConsumer = new ApplicationConsumer(applicationService);

        var applicationRequestDTO = new ApplicationRequestDTO();
        applicationRequestDTO.setJobId(10L);
        applicationRequestDTO.setCoverLetter("Excited to apply");
        applicationRequestDTO.setResumeLink("https://resume.link");

        var userId = 5L;

        requestDTO = new ApplicationRequestAndUserIdDTO(userId, applicationRequestDTO);
    }

    @Test
    void shouldCreateApplicationWhenOrderMessageListener() {
        var consumerRecord = new ConsumerRecord<>(TOPIC_NAME, 0, 0, "key", requestDTO);
        applicationConsumer.applicationMessageListener(consumerRecord);
        verify(applicationService, times(1)).applyApplicantToJob(requestDTO.getUserId(), requestDTO.getApplicationRequestDTO());
    }

    @Test
    void shouldDoNothingWhenOrderMessageListenerWithException() {

        var consumerRecord = new ConsumerRecord<>(TOPIC_NAME, 0, 0, "key", requestDTO);

        doThrow(RuntimeException.class).when(applicationService).applyApplicantToJob(any(), any());

        applicationConsumer.applicationMessageListener(consumerRecord);

        verify(applicationService, times(1)).applyApplicantToJob(requestDTO.getUserId(), requestDTO.getApplicationRequestDTO());
    }
}