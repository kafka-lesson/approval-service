package com.yusufrh.config;

import com.yusufrh.event.ApprovalResultEvent;
import com.yusufrh.event.UserRegisteredEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class SerdeConfig {
    @Bean
    public JsonSerde<UserRegisteredEvent> userRegisteredSerde() {
        return new JsonSerde<>(UserRegisteredEvent.class);
    }

    @Bean
    public JsonSerde<ApprovalResultEvent> approvalSerde() {
        return new JsonSerde<>(ApprovalResultEvent.class);
    }
}
