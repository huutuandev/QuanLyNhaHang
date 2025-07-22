package com.restaurant.management.controller;

import com.restaurant.management.requests.ChatMessageRequest;
import com.restaurant.management.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final IChatService chatService;

    @MessageMapping("/chat/send")
    public void processMessage(
            ChatMessageRequest messageDTO
    ) {
        chatService.handleMessage(messageDTO);
    }

}