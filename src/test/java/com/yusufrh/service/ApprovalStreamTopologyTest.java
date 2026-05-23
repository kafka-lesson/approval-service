package com.yusufrh.service;

import com.yusufrh.event.ApprovalResultEvent;
import com.yusufrh.event.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

    @InjectMocks
    private ApprovalService approvalService;

    private UserRegisteredEvent userEvent;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        userEvent = new UserRegisteredEvent(userId, 25, "test@example.com", "testuser");
    }

    @Test
    void approve_AdultUser_ReturnsApproved() {
        userEvent.setAge(25);

        ApprovalResultEvent result = approvalService.approve(userEvent);

        assertEquals("APPROVED", result.getStatus());
        assertEquals("Adult", result.getReason());
        assertEquals(userEvent.getUserId(), result.getUserId());
        assertEquals(userEvent.getUsername(), result.getUsername());
        assertEquals(userEvent.getEmail(), result.getEmail());
        assertEquals(25, result.getAge());
    }

    @Test
    void approve_UserExactly17_ReturnsApproved() {
        userEvent.setAge(17);

        ApprovalResultEvent result = approvalService.approve(userEvent);

        assertEquals("APPROVED", result.getStatus());
        assertEquals("Adult", result.getReason());
        assertEquals(17, result.getAge());
    }

    @Test
    void approve_UnderageUser_ReturnsRejected() {
        userEvent.setAge(16);

        ApprovalResultEvent result = approvalService.approve(userEvent);

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Underage", result.getReason());
        assertEquals(userEvent.getUserId(), result.getUserId());
        assertEquals(userEvent.getUsername(), result.getUsername());
        assertEquals(userEvent.getEmail(), result.getEmail());
        assertEquals(16, result.getAge());
    }

    @Test
    void approve_VeryYoungUser_ReturnsRejected() {
        userEvent.setAge(5);

        ApprovalResultEvent result = approvalService.approve(userEvent);

        assertEquals("REJECTED", result.getStatus());
        assertEquals("Underage", result.getReason());
        assertEquals(5, result.getAge());
    }

    @Test
    void approve_ElderlyUser_ReturnsApproved() {
        userEvent.setAge(100);

        ApprovalResultEvent result = approvalService.approve(userEvent);

        assertEquals("APPROVED", result.getStatus());
        assertEquals("Adult", result.getReason());
        assertEquals(100, result.getAge());
    }

    @Test
    void approve_PreservesUserData() {
        UUID testUserId = UUID.randomUUID();
        userEvent.setUserId(testUserId);
        userEvent.setAge(30);
        userEvent.setEmail("preserved@example.com");
        userEvent.setUsername("preserveduser");

        ApprovalResultEvent result = approvalService.approve(userEvent);

        assertEquals(testUserId, result.getUserId());
        assertEquals("preserved@example.com", result.getEmail());
        assertEquals("preserveduser", result.getUsername());
        assertEquals(30, result.getAge());
    }
}
