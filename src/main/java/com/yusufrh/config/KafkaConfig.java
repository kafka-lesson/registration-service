package com.yusufrh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class KafkaConfig {

    private KafkaProperties kafkaProperties;
    @Data
    public static class KafkaProperties {
        private String bootstrapServers;
    }
}
