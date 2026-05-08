package com.yusufrh.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import com.yusufrh.config.AppProperties;
import com.yusufrh.entity.UserRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationProducer {

    private final KafkaSender<String, String> kafkaSender;
    private final AppProperties appProperties;
    private final ObjectMapper mapper = new ObjectMapper();

    public Mono<Void> publishRegistrationEvent(UserRegisteredEvent event) {
        return Mono.fromCallable(() -> {
            return mapper.writeValueAsString(event);
        })
        .flatMap(jsonPayload -> {
            ProducerRecord<String, String> record = new ProducerRecord<>(appProperties.getTopics().getRegistrationTopic(), event.getUserId().toString(), jsonPayload);
            SenderRecord<String, String, String> senderRecord = SenderRecord.create(record, event.getUserId().toString());

           return kafkaSender.send(Mono.just(senderRecord)).next();
        })
        .doOnSuccess(result -> System.out.println("Sent event for user: " + event.getUserId()))
        .doOnError(error -> System.err.println("Failed to serialize or send: " + error))
        .then();
    }
}
