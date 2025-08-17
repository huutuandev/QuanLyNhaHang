package com.restaurant.management.service;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.DTO.MessageDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.models.ChatSessionEntity;
import com.restaurant.management.models.MessageEntity;

import java.util.List;

public interface IChatService {
    ChatSessionDTO getOrCreateSession(UserDTO user);
    List<ChatSessionEntity> getAllSessionsForAdmin();
    List<MessageDTO> getMessages(Long sessionId);
    MessageDTO sendMessage(Long sessionId, String phoneNumber, String content);

}
