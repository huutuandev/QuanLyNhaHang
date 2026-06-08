package com.restaurant.management.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionDTO {
    private Long id;
    private String phoneNumber;
    private String fullName;
    private LocalDateTime createdAt;
}