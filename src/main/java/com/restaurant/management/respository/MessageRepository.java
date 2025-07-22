package com.restaurant.management.respository;

import com.restaurant.management.models.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
