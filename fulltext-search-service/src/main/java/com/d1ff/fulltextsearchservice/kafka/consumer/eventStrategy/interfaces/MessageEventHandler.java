package com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.interfaces;

import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.common.avro.MessageType;

public interface MessageEventHandler {
    void processEvent(MessageSentEvent messageEvent);
    MessageType getMessageType();
}
