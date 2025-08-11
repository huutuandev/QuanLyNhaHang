package com.restaurant.management.service;

import com.restaurant.management.DTO.ChatSessionDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.requests.ChatMessageRequest;
import com.restaurant.management.responses.ChatMessageResponse;

import java.util.List;

public interface IChatService {
    void handleMessage(ChatMessageRequest dto);
}
