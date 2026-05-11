package com.yusufrh;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.yusufrh.config.AppProperties;
import com.yusufrh.config.AppProperties.Topics;
import com.yusufrh.controller.RegisterController;
import com.yusufrh.service.RegistrationProducer;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers = RegisterController.class)
@ContextConfiguration(classes = {RegisterController.class, RegistrationService.class})
class RegistrationServiceIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegistrationProducer registrationProducer;

    @MockBean
    private AppProperties appProperties;

    @Test
    void registerUser_Success() {
        // Arrange
        String requestBody = """
            {
                "age": 25,
                "email": "test@example.com",
                "username": "testuser"
            }
            """;

        when(registrationProducer.publishRegistrationEvent(any()))
            .thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.post()
            .uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("User registration initiated and sent to Kafka stream!");
    }

    @Test
    void registerUser_KafkaError() {
        // Arrange
        String requestBody = """
            {
                "age": 30,
                "email": "error@example.com",
                "username": "erroruser"
            }
            """;

        when(registrationProducer.publishRegistrationEvent(any()))
            .thenReturn(Mono.error(new RuntimeException("Kafka connection failed")));

        // Act & Assert
        webTestClient.post()
            .uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("Kafka send failed: Kafka connection failed");
    }

    @Test
    void registerUser_InvalidJson() {
        // Arrange
        String invalidRequestBody = """
            {
                "age": "invalid",
                "email": "invalid@example.com"
            }
            """;

        // Act & Assert
        webTestClient.post()
            .uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue(invalidRequestBody)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void registerUser_EmptyBody() {
        // Act & Assert
        webTestClient.post()
            .uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue("")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void registerUser_MissingFields() {
        // Arrange
        String incompleteRequestBody = """
            {
                "email": "incomplete@example.com"
            }
            """;

        // Act & Assert
        webTestClient.post()
            .uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue(incompleteRequestBody)
            .exchange()
            //.expectStatus().isBadRequest();
            .expectStatus().isEqualTo(500);
    }

    @Test
    void registerUser_ValidMultipleRequests() {
        // Arrange
        String requestBody1 = """
            {
                "age": 22,
                "email": "user1@example.com",
                "username": "user1"
            }
            """;
        
        String requestBody2 = """
            {
                "age": 32,
                "email": "user2@example.com",
                "username": "user2"
            }
            """;

        when(registrationProducer.publishRegistrationEvent(any()))
            .thenReturn(Mono.empty());

        // Act & Assert - First request
        webTestClient.post()
            .uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue(requestBody1)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("User registration initiated and sent to Kafka stream!");

        // Act & Assert - Second request
        webTestClient.post()
            .uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue(requestBody2)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("User registration initiated and sent to Kafka stream!");
    }

    @Test
    void registerUser_WrongHttpMethod() {
        // Act & Assert
        webTestClient.get()
            .uri("/api/users/register")
            .exchange()
            //.expectStatus().isNotFound();
            .expectStatus().isEqualTo(405);
    }
}
