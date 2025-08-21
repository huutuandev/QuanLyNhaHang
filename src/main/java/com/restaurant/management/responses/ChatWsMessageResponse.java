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
    private Long tempId;

}
