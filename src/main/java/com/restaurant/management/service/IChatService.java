package com.restaurant.management.service;

import com.restaurant.management.requests.ChatMessageRequest;

public interface IChatService {
    void handleMessage(ChatMessageRequest dto);
}
