package com.yusufrh.service;

import com.yusufrh.config.TopicProperties;
import com.yusufrh.event.ApprovalResultEvent;
import com.yusufrh.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
 
@Component
@RequiredArgsConstructor
@Slf4j
public class ApprovalStreamTopology {
        private final JsonSerde<UserRegisteredEvent> userSerde;
        private final JsonSerde<ApprovalResultEvent> approvalSerde;
        private final TopicProperties topicProperties;

        @Bean
        public KStream<String, UserRegisteredEvent> approvalTopology(StreamsBuilder builder) {
                KStream<String, UserRegisteredEvent> stream = builder.stream(topicProperties.getTopics().getRegistrationTopic(), Consumed.with(Serdes.String(), userSerde));
                KStream<String, ApprovalResultEvent> approved = stream.mapValues(this::approve);
                approved.to(topicProperties.getTopics().getApprovalTopic(), Produced.with(Serdes.String(), approvalSerde));
                return stream;
        }

        private ApprovalResultEvent approve(UserRegisteredEvent event) {

                log.info("Processing user {}", event.getUserId());

                if (event.getAge() >= 17) {
                        return ApprovalResultEvent.builder()
                                .userId(event.getUserId())
                                .username(event.getUsername())
                                .email(event.getEmail())
                                .age(event.getAge())
                                .status("APPROVED")
                                .reason("Adult")
                                .build();
                
                }

                return ApprovalResultEvent.builder()
                                .userId(event.getUserId())
                                .username(event.getUsername())
                                .email(event.getEmail())
                                .age(event.getAge())
                                .status("REJECTED")
                                .reason("Underage")
                                .build();


        }
}
