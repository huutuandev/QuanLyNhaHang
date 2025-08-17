package com.restaurant.management.DTO;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionDTO {
    private Long id;
    private Long userId;                // thay cho UserEntity
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private List<MessageDTO> messages;  // danh sách message
}