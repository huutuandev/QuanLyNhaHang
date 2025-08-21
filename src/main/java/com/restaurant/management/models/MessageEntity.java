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
    @JoinColumn(name = "SessionId", nullable = false)
    private ChatSessionEntity session;

    @Column(name = "SenderPhone", nullable = true, length = 50)
    private String senderPhone;

    @Column(name = "Content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "SentAt")
    private LocalDateTime sentAt;
}
