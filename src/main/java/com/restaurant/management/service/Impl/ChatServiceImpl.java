package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.models.UserEntity;
import com.restaurant.management.requests.ChatMessageRequest;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.ChatSessionEntity;
import com.restaurant.management.models.MessageEntity;
import com.restaurant.management.respository.ChatSessionRepository;
import com.restaurant.management.respository.MessageRepository;
import com.restaurant.management.respository.UserRepository;
import com.restaurant.management.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final MessageRepository messageRepo;
    private final ChatSessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final SimpMessagingTemplate messagingTemplate;


    @Override
    @Transactional
    public void handleMessage(ChatMessageRequest dto) {
        ChatSessionEntity session = sessionRepo.findById(dto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session không tồn tại"));
        MessageEntity saved = messageRepo.save(
                MessageEntity.builder()
                        .chatSession(session)
                        .messageText(dto.getMessageText())
//                        .sender()
                        .build()
        );
        ChatMessageRequest out = ChatMessageRequest.builder()
                .id(saved.getId())
                .sessionId(session.getId())
//                .senderName(saved.getSenderName())
                .messageText(saved.getMessageText())
                .build();
        messagingTemplate.convertAndSend("/topic/chat/" + session.getId(), out);
    }
}
