package com.yusufrh.controller;

import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yusufrh.entity.UserRegisteredEvent;
import com.yusufrh.service.RegistrationProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class RegisterController {

    private final RegistrationProducer registrationProducer;

    @PostMapping("/register")
    public Mono<String> registerUser(@RequestBody UserRegisteredEvent user) {
        log.info("<- incoming user registration request  {}", user.getEmail());
        
        UserRegisteredEvent event = new UserRegisteredEvent(UUID.randomUUID(), user.getAge(), user.getEmail(), user.getUsername());

        return registrationProducer.publishRegistrationEvent(event)
                .thenReturn("User registration initiated and sent to Kafka stream!")
                .onErrorResume(ex ->
                    Mono.just("Kafka send failed: " + ex.getMessage())
                    );
    }

}

