package com.restaurant.management.controller;

import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.requests.ChatMessageRequest;
import com.restaurant.management.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final IChatService chatService;

    @MessageMapping("/chat/send")
    public void processMessage(@Valid @RequestBody
            ChatMessageRequest messageDTO, @AuthenticationPrincipal UserDTO userDTO, Principal principal
            ) {
        chatService.handleMessage(messageDTO);
    }

}