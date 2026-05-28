package com.restaurant.management.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatWsMessageRequest {
    private String content;     // Nội dung tin nhắn
    private String phoneNumber; // Lấy từ JWT client (người gửi)
    private Long sessionId;
    private Long tempId; // ✅ thêm để echo lại cho FE
}
