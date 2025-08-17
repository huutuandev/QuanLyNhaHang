package com.restaurant.management.responses;


import com.restaurant.management.DTO.MessageDTO;
import com.restaurant.management.models.MessageEntity;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatWsMessageResponse {
    private Long id;
    private String content;
    private String senderPhone;
    private Long sessionId;
    private LocalDateTime sentAt;

    public static ChatWsMessageResponse fromEntity(MessageDTO message) {
        ChatWsMessageResponse response = new ChatWsMessageResponse();
        response.setId(message.getId());
        response.setContent(message.getMessageText());
        response.setSenderPhone(message.getSenderPhone());
        response.setSessionId(message.getSessionId());
        response.setSentAt(message.getSentAt());
        return response;
    }
}
