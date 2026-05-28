package com.restaurant.management.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long sessionId;
    private String senderPhone;
    private String fullName;
    private String content;
    private LocalDateTime sentAt;
}