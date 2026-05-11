package com.yusufrh.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Configuration
@RequiredArgsConstructor
public class KafkaReceiverConfig {

    private final TopicProperties topicProperties;
    private final KafkaConfig kafkaConfig;
    
     @Bean
    public KafkaReceiver<String, String> kafkaReceiver() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaProperties().getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, topicProperties.getTopics().getApprovalGroup());
        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.<String, String>create(props)
            .subscription(Collections.singleton(topicProperties.getTopics().getRegistrationTopic()));

        return KafkaReceiver.create(receiverOptions);
    }

}
