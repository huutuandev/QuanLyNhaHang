package com.restaurant.management.service;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.DTO.MessageDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.models.ChatSessionEntity;
import com.restaurant.management.models.MessageEntity;

import java.util.List;

public interface IChatService {
    ChatSessionDTO createSessionIfNotExists(String phone, String fullName);
    MessageDTO saveMessage(Long sessionId, String senderPhone, String content);
    List<ChatSessionDTO> getAllSessions();
    List<MessageDTO> getMessagesBySession(Long sessionId);

}
