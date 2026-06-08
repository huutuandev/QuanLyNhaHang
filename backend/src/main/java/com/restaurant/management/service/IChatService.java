package com.restaurant.management.service;

import com.restaurant.management.dto.ChatSessionDTO;
import com.restaurant.management.dto.MessageDTO;

import java.util.List;

public interface IChatService {
    ChatSessionDTO createSessionIfNotExists(String phone, String fullName);
    MessageDTO saveMessage(Long sessionId, String senderPhone, String content);
    List<ChatSessionDTO> getAllSessions();
    List<MessageDTO> getMessagesBySession(Long sessionId);

}
