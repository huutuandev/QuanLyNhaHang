package com.restaurant.management.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long sessionId;       // thay cho ChatSessionEntity
    private String senderPhone;   // thay cho UserEntity
    private String messageText;
    private LocalDateTime sentAt;
}
