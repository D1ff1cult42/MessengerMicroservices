package com.d1ff.mailservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "outbox-events")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    private String aggregateId;
    private String topic;
    private byte[] payload;
    @CreationTimestamp
    private LocalDateTime timestamp;
    @Builder.Default
    private boolean sent = false;
}