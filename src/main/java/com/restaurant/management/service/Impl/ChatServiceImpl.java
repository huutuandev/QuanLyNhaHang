package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.DTO.MessageDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.constant.RoleConstants;
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
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final ChatSessionRepository chatSessionRepo;
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ChatSessionDTO getOrCreateSession(UserDTO userDTO) {
        List<ChatSessionEntity> sessions = chatSessionRepo.findByUser_PhoneNumber(userDTO.getPhoneNumber());
        if (!sessions.isEmpty()) {
            return modelMapper.map(sessions.get(0), ChatSessionDTO.class);
        }

        UserEntity userEntity = userRepo.findByPhoneNumber(userDTO.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatSessionEntity newSession = ChatSessionEntity.builder()
                .user(userEntity)
                .build();

        ChatSessionEntity saved = chatSessionRepo.save(newSession);
        return modelMapper.map(saved, ChatSessionDTO.class);
    }



    @Override
    public List<ChatSessionEntity> getAllSessionsForAdmin() {
        return chatSessionRepo.findAll();
    }

    @Override
    public List<MessageDTO> getMessages(Long sessionId) {
        List<MessageEntity> message = messageRepo.findByChatSessionIdOrderBySentAtAsc(sessionId);
        return message.stream().map(messageEntity ->
                modelMapper.map(messageEntity, MessageDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageDTO sendMessage(Long sessionId, String phoneNumber, String content) {
        ChatSessionEntity session = chatSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        UserEntity userEntity = userRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        MessageEntity msg = MessageEntity.builder()
                .chatSession(session)
                .sender(userEntity)
                .messageText(content)
                .build();
        MessageEntity saved = messageRepo.save(msg);
        return modelMapper.map(saved, MessageDTO.class);
    }
}

