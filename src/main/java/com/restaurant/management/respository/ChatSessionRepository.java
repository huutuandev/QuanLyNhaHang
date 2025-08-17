package com.restaurant.management.respository;

import com.restaurant.management.models.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, Long>{
    List<ChatSessionEntity> findByUser_PhoneNumber(String phoneNumber);

}
