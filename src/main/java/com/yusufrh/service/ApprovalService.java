package com.yusufrh.service;

import com.yusufrh.event.ApprovalResultEvent;
import com.yusufrh.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApprovalService {

    public ApprovalResultEvent approve(UserRegisteredEvent event) {
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
