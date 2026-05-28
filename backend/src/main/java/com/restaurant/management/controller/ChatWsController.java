//package com.restaurant.management.controller;
//
//import com.restaurant.management.DTO.MessageDTO;
//import com.restaurant.management.requests.ChatWsMessageRequest;
//import com.restaurant.management.responses.ChatWsMessageResponse;
//import com.restaurant.management.service.IChatService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//
//@Controller
//@RequiredArgsConstructor
//public class ChatWsController {
//    private final IChatService chatService;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @MessageMapping("/chat.send")
//    public void send(ChatWsMessageRequest req) {
//        if (req.getPhoneNumber() == null) return;
//        MessageDTO saved = chatService.sendMessage(req.getSessionId(), req.getPhoneNumber(), req.getContent());
//        ChatWsMessageResponse resp = ChatWsMessageResponse.fromEntity(saved);
//        resp.setTempId(req.getTempId());
//        messagingTemplate.convertAndSend("/topic/chat." + req.getSessionId(), resp);
//        messagingTemplate.convertAndSend("/topic/admin.chat", resp);
//    }
//}
