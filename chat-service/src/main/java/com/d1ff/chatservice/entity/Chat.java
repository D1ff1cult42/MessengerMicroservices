package com.d1ff.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(nullable = false)
    private String name;

    @Column(name = "icon_object_name")
    private String iconObjectName;

    @Column(name = "icon_bucket_name")
    private String iconBucketName;

    @Column(name = "icon_file_size")
    private Long iconFileSize;

    @Column(name = "icon_updated_at")
    private LocalDateTime iconUpdatedAt;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_group_chat", nullable = false)
    private boolean isGroupChat;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> participants = new ArrayList<>();

    public void addParticipant(UUID userId, ChatRole role) {
        ChatParticipant participant = ChatParticipant.builder()
                .chat(this)
                .userId(userId)
                .role(role)
                .build();
        participants.add(participant);
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        if (this.iconObjectName != null) {
            this.iconUpdatedAt = LocalDateTime.now();
        }
    }
}