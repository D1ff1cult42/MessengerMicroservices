package com.d1ff.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_participants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "user_id"}))
public class ChatParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "is_muted")
    private boolean isMuted;

    @PrePersist
    public void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }
}
