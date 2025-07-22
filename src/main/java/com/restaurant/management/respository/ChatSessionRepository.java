package com.restaurant.management.respository;

import com.restaurant.management.models.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, Long>{
}
