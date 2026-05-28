package com.restaurant.management.controller;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.DTO.MessageDTO;
import com.restaurant.management.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final IChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // User gửi tin nhắn
    @MessageMapping("/sendMessage")
    public void sendMessage(MessageDTO msg) {
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
        MessageDTO saved = chatService.saveMessage(msg.getSessionId(), msg.getSenderPhone(), msg.getContent());

        // Gửi lại cho user theo session riêng
        messagingTemplate.convertAndSend("/topic/session/" + msg.getSessionId(), saved);
    }

    // API admin lấy tất cả sessions
    @GetMapping("/sessions")
    public List<ChatSessionDTO> getAllSessions() {
        return chatService.getAllSessions();
    }

    // API admin xem lịch sử chat
    @GetMapping("/sessions/{id}/messages")
    public List<MessageDTO> getMessagesBySession(@PathVariable Long id) {
        return chatService.getMessagesBySession(id);
    }
}
