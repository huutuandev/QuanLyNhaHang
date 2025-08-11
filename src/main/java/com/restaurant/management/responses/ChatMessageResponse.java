package com.restaurant.management.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String sender;         // tên hoặc số điện thoại người gửi
    private String content;        // nội dung tin nhắn
    private LocalDateTime timestamp; // thời gian gửi
}
