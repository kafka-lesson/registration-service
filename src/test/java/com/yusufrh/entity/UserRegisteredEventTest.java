package com.yusufrh.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserRegisteredEventTest {

    @Test
    void constructor_SetsAllFields() {
        // Arrange
        UUID userId = UUID.randomUUID();
        int age = 25;
        String email = "test@example.com";
        String username = "testuser";

        // Act
        UserRegisteredEvent event = new UserRegisteredEvent(userId, age, email, username);

        // Assert
        assertEquals(userId, event.getUserId());
        assertEquals(age, event.getAge());
        assertEquals(email, event.getEmail());
        assertEquals(username, event.getUsername());
    }

    @Test
    void constructor_WithNullUserId() {
        // Arrange
        UUID userId = null;
        int age = 30;
        String email = "nulluser@example.com";
        String username = "nulluser";

        // Act
        UserRegisteredEvent event = new UserRegisteredEvent(userId, age, email, username);

        // Assert
        assertNull(event.getUserId());
        assertEquals(age, event.getAge());
        assertEquals(email, event.getEmail());
        assertEquals(username, event.getUsername());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        UserRegisteredEvent event = new UserRegisteredEvent(null, 0, null, null);
        UUID userId = UUID.randomUUID();
        int age = 35;
        String email = "setter@example.com";
        String username = "setteruser";

        // Act
        event.setUserId(userId);
        event.setAge(age);
        event.setEmail(email);
        event.setUsername(username);

        // Assert
        assertEquals(userId, event.getUserId());
        assertEquals(age, event.getAge());
        assertEquals(email, event.getEmail());
        assertEquals(username, event.getUsername());
    }

    @Test
    void toString_ReturnsValidString() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserRegisteredEvent event = new UserRegisteredEvent(userId, 28, "tostring@example.com", "tostringuser");

        // Act
        String result = event.toString();

        // Assert
        assertNotNull(result);
        assertEquals(result.contains(userId.toString()), true);
        assertEquals(result.contains("tostring@example.com"), true);
        assertEquals(result.contains("tostringuser"), true);
        assertEquals(result.contains("28"), true);
    }

    @Test
    void multipleEvents_WithDifferentData() {
        // Arrange & Act
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        
        UserRegisteredEvent event1 = new UserRegisteredEvent(userId1, 22, "user1@example.com", "user1");
        UserRegisteredEvent event2 = new UserRegisteredEvent(userId2, 32, "user2@example.com", "user2");

        // Assert
        assertEquals(userId1, event1.getUserId());
        assertEquals(22, event1.getAge());
        assertEquals("user1@example.com", event1.getEmail());
        assertEquals("user1", event1.getUsername());

        assertEquals(userId2, event2.getUserId());
        assertEquals(32, event2.getAge());
        assertEquals("user2@example.com", event2.getEmail());
        assertEquals("user2", event2.getUsername());
    }

    @Test
    void constructor_WithEmptyStrings() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String emptyEmail = "";
        String emptyUsername = "";

        // Act
        UserRegisteredEvent event = new UserRegisteredEvent(userId, 25, emptyEmail, emptyUsername);

        // Assert
        assertEquals(userId, event.getUserId());
        assertEquals(25, event.getAge());
        assertEquals(emptyEmail, event.getEmail());
        assertEquals(emptyUsername, event.getUsername());
    }
}
