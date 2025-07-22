package com.restaurant.management.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageRequest {
    private Long id;
    private Long sessionId;
    private Long senderId;
    private String senderName;
    private String messageText;
}
