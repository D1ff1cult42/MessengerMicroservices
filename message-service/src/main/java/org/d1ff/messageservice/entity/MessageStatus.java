package org.d1ff.messageservice.entity;

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
@Table(name = "message_statuses",
       uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"}))
public class MessageStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_status_seq")
    @SequenceGenerator(name = "message_status_seq", sequenceName = "message_statuses_id_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.SENT;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
