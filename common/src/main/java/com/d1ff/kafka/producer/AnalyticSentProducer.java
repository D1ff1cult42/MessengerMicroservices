package com.d1ff.kafka.producer;

import com.d1ff.common.avro.AnalyticSentEvent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;
abstract public class AnalyticSentProducer {

    public AnalyticSentEvent makeAnalyticEvent(byte[] payload) {
        return AnalyticSentEvent.newBuilder()
                .setEventId(UUID.randomUUID())
                .setCreatedAt(Instant.now())
                .setPayload(ByteBuffer.wrap(payload))
                .build();
    }

    protected byte[] toOutboxPayload(AnalyticSentEvent event) throws IOException{
        return event.toByteBuffer().array();
    }

    abstract public void saveAnalytic(byte[] jsonPayload);
}
