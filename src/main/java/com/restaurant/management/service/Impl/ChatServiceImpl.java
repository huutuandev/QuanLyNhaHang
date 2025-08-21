package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.DTO.MessageDTO;
import com.restaurant.management.models.ChatSessionEntity;
import com.restaurant.management.models.MessageEntity;
import com.restaurant.management.respository.ChatSessionRepository;
import com.restaurant.management.respository.MessageRepository;
import com.restaurant.management.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final ChatSessionRepository chatSessionRepo;
    private final MessageRepository messageRepo;
    private final ModelMapper modelMapper;

    // Tạo session mới khi user nhắn lần đầu
    public ChatSessionDTO createSessionIfNotExists(String phone, String fullName) {
        ChatSessionEntity session = ChatSessionEntity.builder()
                .phoneNumber(phone)
                .fullName(fullName)
                .createdAt(LocalDateTime.now())
                .build();
        return modelMapper.map(chatSessionRepo.save(session), ChatSessionDTO.class);
    }

    // Lưu tin nhắn
    public MessageDTO saveMessage(Long sessionId, String senderPhone, String content) {
        ChatSessionEntity session = chatSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        MessageEntity message = MessageEntity.builder()
                .session(session)
                .senderPhone(senderPhone)
                .content(content)
                .sentAt(LocalDateTime.now())
                .build();
        return modelMapper.map(messageRepo.save(message), MessageDTO.class);
    }

    // Lấy tất cả sessions (admin)
    public List<ChatSessionDTO> getAllSessions() {
        return chatSessionRepo.findAll().stream()
                .map(s -> modelMapper.map(s, ChatSessionDTO.class))
                .collect(Collectors.toList());
    }

    // Lấy lịch sử 1 session
    public List<MessageDTO> getMessagesBySession(Long sessionId) {
        return messageRepo.findBySessionId(sessionId).stream()
                .map(m -> modelMapper.map(m, MessageDTO.class))
                .collect(Collectors.toList());
    }
}
