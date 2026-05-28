package com.restaurant.management.responses;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.models.ChatSessionEntity;
import com.restaurant.management.DTO.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatSessionResponse {
    private ChatSessionDTO session;
    private List<MessageDTO> messages;
}
