package com.d1ff.fulltextsearchservice.kafka.consumer;

import com.d1ff.common.avro.MessageType;
import com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.interfaces.MessageEventHandler;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageEventHandlerFactory {
    private final Map<MessageType, MessageEventHandler> handlers = new EnumMap<>(MessageType.class);

    public MessageEventHandlerFactory(List<MessageEventHandler> handlerList) {
        for (MessageEventHandler handler : handlerList) {
            MessageEventHandler previous = handlers.put(handler.getMessageType(), handler);
            if (previous != null) {
                throw new IllegalStateException("Duplicate handler for messageType: " + handler.getMessageType());
            }
        }
    }

    public MessageEventHandler getHandler(MessageType messageType) {
        MessageEventHandler handler = handlers.get(messageType);
        if (handler == null) {
            throw new IllegalArgumentException("No handler for messageType: " + messageType);
        }
        return handler;
    }
}
