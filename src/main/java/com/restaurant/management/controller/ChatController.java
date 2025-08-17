package com.restaurant.management.controller;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.DTO.MessageDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.models.ChatSessionEntity;
import com.restaurant.management.responses.ChatSessionResponse;
import com.restaurant.management.service.IChatService;
import com.restaurant.management.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final IChatService chatService;
    private final IUserService userService;

    @GetMapping("/session")
    public ResponseEntity<?> getOrCreateChat(@AuthenticationPrincipal UserDTO userDTO) {
        ChatSessionDTO session = chatService.getOrCreateSession(userDTO);
        return ResponseEntity.ok(session);
    }

    // Admin: lấy tất cả sessions
    @GetMapping("/sessions")
    public ResponseEntity<?> getAllSessions() {
        return ResponseEntity.ok(chatService.getAllSessionsForAdmin());
    }

    // Admin: xem tin nhắn 1 session cụ thể
    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatService.getMessages(sessionId));
    }

}
