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
    private String phoneNumber;
    private String fullName;
    private LocalDateTime createdAt;
}