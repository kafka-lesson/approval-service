package com.yusufrh.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.yusufrh.config.TopicProperties;
import com.yusufrh.entity.ApprovalResult;
import com.yusufrh.entity.UserRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApprovalProcessor implements CommandLineRunner {

    private final KafkaReceiver<String, String> receiver;
    private final KafkaSender<String, String> sender;
    private final ObjectMapper mapper = new ObjectMapper();
    private final TopicProperties appProperties;
    
    @SuppressWarnings("unchecked")
    @Override
    public void run(String... args) {

        receiver.receive()
            .flatMap(record ->
                Mono.fromCallable(() ->
                    mapper.readValue(record.value(), UserRegisteredEvent.class)
                )
                .map(json -> {
                    String status = json.getAge() >= 17 ? "APPROVED" : "DENIED";
                    Map<String, Object> output = new HashMap<>();
                    output.put("userId", json.getUserId());
                    output.put("status", status);
                    output.put("username", json.getUsername());
                    output.put("email", json.getEmail());
                    output.put("age",  json.getAge());

                    String payload = mapper.writeValueAsString(output);

                    return new ApprovalResult(json.getUserId(), payload);
                })
                .doOnNext(v ->
                    log.info("🚀 ONPROCESS: {}", v.userId().toString())
                )
                .flatMap(result ->
                    sender.send(Mono.just(SenderRecord.create(new ProducerRecord<>(appProperties.getTopics().getApprovalTopic(), result.payload()), result.userId().toString()))                            )
                    .next()
                )
                .doOnNext(r -> record.receiverOffset().acknowledge())
                .doOnNext(v -> log.info("✅ PROCESSED => {}", v.correlationMetadata()) )
                .onErrorResume(e -> {
                    log.error("❌ ERROR PROCESSING: {}", record.value(), e);
                    return Mono.empty();
                })
            )
            .subscribe(
                    v -> {},
                    err -> log.error("🔥 PIPELINE ERROR", err)
            );
    }
}
