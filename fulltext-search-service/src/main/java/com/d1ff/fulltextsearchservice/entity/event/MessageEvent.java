package com.d1ff.fulltextsearchservice.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {
    private EventType type;
    private String messageId;
    private String chatId;
    private String text;
    private Instant createdAt;

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}