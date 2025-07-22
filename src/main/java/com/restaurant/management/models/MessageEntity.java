package com.restaurant.management.models;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ChatSessionId")
    private ChatSessionEntity chatSession;

    @Column(name = "SenderName")
    private String senderName;

    @ManyToOne
    @JoinColumn(name = "SenderId")
    private UserEntity sender;

    @Column(name = "MessageText")
    private String messageText;

    @Column(name = "SentAt")
    private LocalDateTime sentAt;

    @PrePersist
    public void prePersist() {
        if (sentAt == null) sentAt = LocalDateTime.now();
    }
}