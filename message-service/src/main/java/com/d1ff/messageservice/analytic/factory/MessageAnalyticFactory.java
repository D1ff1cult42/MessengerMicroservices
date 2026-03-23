package com.d1ff.messageservice.analytic.factory;

import com.d1ff.messageservice.analytic.enums.AnalyticEventType;
import com.d1ff.messageservice.analytic.payload.MessageAnalyticPayload;
import com.d1ff.messageservice.entity.Message;
import com.d1ff.utils.JsonUtils;


import java.time.Instant;
import java.util.UUID;

public class MessageAnalyticFactory {
    static public byte[] createPayload(String ip,
                                       String userAgent,
                                       AnalyticEventType eventType,
                                       Message message){
        MessageAnalyticPayload payload = new MessageAnalyticPayload(
                message.getId(),
                message.getFromUser(),
                message.getChatId(),
                eventType,
                Instant.now(),
                ip,
                userAgent,
                (long) message.getContent().length(),
                message.getBucketName() != null
        );

        return JsonUtils.toBytes(payload);
    }
}
