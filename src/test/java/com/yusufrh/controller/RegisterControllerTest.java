package com.yusufrh.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yusufrh.entity.UserRegisteredEvent;
import com.yusufrh.service.RegistrationProducer;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    @Mock
    private RegistrationProducer registrationProducer;

    @InjectMocks
    private RegisterController registerController;

    @Test
    void registerUser_Success() {
        // Arrange
        UserRegisteredEvent inputUser = new UserRegisteredEvent(null, 25, "test@example.com", "testuser");
        UserRegisteredEvent expectedEvent = new UserRegisteredEvent(UUID.randomUUID(), 25, "test@example.com", "testuser");
        
        when(registrationProducer.publishRegistrationEvent(any(UserRegisteredEvent.class)))
            .thenReturn(Mono.empty());

        // Act
        Mono<String> result = registerController.registerUser(inputUser);

        // Assert
        StepVerifier.create(result)
            .expectNext("User registration initiated and sent to Kafka stream!")
            .verifyComplete();
    }

    @Test
    void registerUser_KafkaError() {
        // Arrange
        UserRegisteredEvent inputUser = new UserRegisteredEvent(null, 25, "test@example.com", "testuser");
        String errorMessage = "Kafka connection failed";
        
        when(registrationProducer.publishRegistrationEvent(any(UserRegisteredEvent.class)))
            .thenReturn(Mono.error(new RuntimeException(errorMessage)));

        // Act
        Mono<String> result = registerController.registerUser(inputUser);

        // Assert
        StepVerifier.create(result)
            .expectNext("Kafka send failed: " + errorMessage)
            .verifyComplete();
    }

    @Test
    void registerUser_ValidInput() {
        // Arrange
        UserRegisteredEvent inputUser = new UserRegisteredEvent(null, 30, "valid@example.com", "validuser");
        
        when(registrationProducer.publishRegistrationEvent(any(UserRegisteredEvent.class)))
            .thenReturn(Mono.empty());

        // Act
        Mono<String> result = registerController.registerUser(inputUser);

        // Assert
        StepVerifier.create(result)
            .expectNext("User registration initiated and sent to Kafka stream!")
            .verifyComplete();
    }

    @Test
    void registerUser_GeneratesUUID() {
        // Arrange
        UserRegisteredEvent inputUser = new UserRegisteredEvent(null, 25, "uuid@test.com", "uuiduser");
        
        when(registrationProducer.publishRegistrationEvent(any(UserRegisteredEvent.class)))
            .thenAnswer(invocation -> {
                UserRegisteredEvent event = invocation.getArgument(0);
                assertEquals(event.getUserId().getClass(), UUID.class);
                return Mono.empty();
            });

        // Act & Assert
        StepVerifier.create(registerController.registerUser(inputUser))
            .expectNext("User registration initiated and sent to Kafka stream!")
            .verifyComplete();
    }
}
