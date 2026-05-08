package com.yusufrh.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yusufrh.config.AppProperties;
import com.yusufrh.entity.UserRegisteredEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RegistrationProducerTest {

    @Mock
    private KafkaSender<String, String> kafkaSender;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.Topics topics;

    @Mock
    private SenderResult<String> senderResult;

    @InjectMocks
    private RegistrationProducer registrationProducer;

    @Test
    void publishRegistrationEvent_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserRegisteredEvent event = new UserRegisteredEvent(userId, 25, "test@example.com", "testuser");
        String topicName = "user-registration";
        
        when(appProperties.getTopics()).thenReturn(topics);
        when(topics.getRegistrationTopic()).thenReturn(topicName);
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.just(senderResult));

        // Act
        Mono<Void> result = registrationProducer.publishRegistrationEvent(event);

        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        verify(kafkaSender).send(any(Mono.class));
        verify(appProperties).getTopics();
        verify(topics).getRegistrationTopic();
    }

    @Test
    void publishRegistrationEvent_KafkaSendError() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserRegisteredEvent event = new UserRegisteredEvent(userId, 25, "test@example.com", "testuser");
        String topicName = "user-registration";
        String errorMessage = "Kafka broker not available";
        
        when(appProperties.getTopics()).thenReturn(topics);
        when(topics.getRegistrationTopic()).thenReturn(topicName);
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.error(new RuntimeException(errorMessage)));

        // Act
        Mono<Void> result = registrationProducer.publishRegistrationEvent(event);

        // Assert
        StepVerifier.create(result)
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void publishRegistrationEvent_ValidEventSerialization() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserRegisteredEvent event = new UserRegisteredEvent(userId, 30, "valid@example.com", "validuser");
        String topicName = "user-registration";
        
        when(appProperties.getTopics()).thenReturn(topics);
        when(topics.getRegistrationTopic()).thenReturn(topicName);
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.just(senderResult));

        // Act
        Mono<Void> result = registrationProducer.publishRegistrationEvent(event);

        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        verify(kafkaSender).send(any(Mono.class));
    }

    @Test
    void publishRegistrationEvent_DifferentUserEvents() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UserRegisteredEvent event1 = new UserRegisteredEvent(userId1, 25, "user1@example.com", "user1");
        UserRegisteredEvent event2 = new UserRegisteredEvent(userId2, 30, "user2@example.com", "user2");
        String topicName = "user-registration";
        
        when(appProperties.getTopics()).thenReturn(topics);
        when(topics.getRegistrationTopic()).thenReturn(topicName);
        when(kafkaSender.send(any(Mono.class))).thenReturn(Flux.just(senderResult));

        // Act & Assert - First event
        StepVerifier.create(registrationProducer.publishRegistrationEvent(event1))
            .verifyComplete();
        
        // Act & Assert - Second event
        StepVerifier.create(registrationProducer.publishRegistrationEvent(event2))
            .verifyComplete();
        
        // Verify kafkaSender.send was called twice
        verify(kafkaSender, times(2)).send(any(Mono.class));

    }
}
