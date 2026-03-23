package com.d1ff.analyticservice.kafka.consumer;


import com.d1ff.analyticservice.service.AnalyticRouterService;
import com.d1ff.common.avro.AnalyticSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticSentConsumer {

    private final AnalyticRouterService analyticRouterService;

    @KafkaListener(topics = "analytic-sent", groupId = "analytic-service-group")
    public void consume(ConsumerRecord<String, AnalyticSentEvent> record, Acknowledgment ack) {
        AnalyticSentEvent event = record.value();
        log.debug("Received analytic event: id={}", event.getEventId());

        ByteBuffer buf = event.getPayload();
        byte[] bytes = new byte[buf.remaining()];
        buf.duplicate().get(bytes);
        analyticRouterService.route(bytes);

        ack.acknowledge();
    }
}