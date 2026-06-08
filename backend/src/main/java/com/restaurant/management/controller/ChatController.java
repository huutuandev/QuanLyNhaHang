package com.restaurant.management.controller;

import com.restaurant.management.dto.ChatSessionDTO;
import com.restaurant.management.dto.MessageDTO;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.service.IChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "Endpoints and WebSocket handlers for live customer support chat")
public class ChatController {
    private final IChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // User gửi tin nhắn
    @MessageMapping("/sendMessage")
    public void sendMessage(MessageDTO msg) {
        log.info("WebSocket: User {} sending message: {}", msg.getSenderPhone(), msg.getContent());
        // Nếu chưa có session thì tạo
        ChatSessionDTO session = chatService.createSessionIfNotExists(msg.getSenderPhone(), "Guest");

        // Lưu tin nhắn
        MessageDTO saved = chatService.saveMessage(session.getId(), msg.getSenderPhone(), msg.getContent());

        // Bắn realtime cho admin (topic riêng cho admin)
        messagingTemplate.convertAndSend("/topic/admin", saved);
    }

    // Admin gửi tin nhắn vào session cụ thể
    @MessageMapping("/adminSend")
    public void adminSend(MessageDTO msg) {
        log.info("WebSocket: Admin sending message to Session ID: {}", msg.getSessionId());
        MessageDTO saved = chatService.saveMessage(msg.getSessionId(), msg.getSenderPhone(), msg.getContent());

        // Gửi lại cho user theo session riêng
        messagingTemplate.convertAndSend("/topic/session/" + msg.getSessionId(), saved);
    }

    // API admin lấy tất cả sessions
    @GetMapping("/sessions")
    @Operation(summary = "Get all chat sessions", description = "Retrieves all support chat sessions (Admin only).")
    public ResponseEntity<ApiResponse<List<ChatSessionDTO>>> getAllSessions() {
        log.info("Fetching all active support chat sessions");
        List<ChatSessionDTO> sessions = chatService.getAllSessions();
        return ResponseEntity.ok(ApiResponse.success("Sessions retrieved successfully", sessions));
    }

    // API admin xem lịch sử chat
    @GetMapping("/sessions/{id}/messages")
    @Operation(summary = "Get messages for a session", description = "Retrieves message history for a specific chat session (Admin only).")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessagesBySession(@PathVariable Long id) {
        log.info("Fetching message history for Session ID: {}", id);
        List<MessageDTO> messages = chatService.getMessagesBySession(id);
        return ResponseEntity.ok(ApiResponse.success("Message history retrieved successfully", messages));
    }
}
