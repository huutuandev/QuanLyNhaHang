package com.restaurant.management.respository;

import com.restaurant.management.models.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    // Lấy danh sách tin nhắn theo phòng chat, sắp xếp tăng dần theo thời gian gửi
    List<MessageEntity> findByChatSessionIdOrderBySentAtAsc(Long chatSessionId);
}
