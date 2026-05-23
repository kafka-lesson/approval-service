package com.yusufrh.event;

import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalResultEvent {
    UUID userId;
    int age;
    String email;
    String username;
    String status;
    String reason;
}
