package com.d1ff.messageservice.util;

import lombok.RequiredArgsConstructor;
import com.d1ff.bucket.BucketResolver;
import com.d1ff.messageservice.entity.MessageType;
import org.springframework.stereotype.Component;

/**
 * Message-service specific helper.
 * Uses the universal {@link BucketResolver} from common to get the bucket,
 * and derives {@link MessageType} from the bucket name convention {@code message_<type>}.
 */
@Component
@RequiredArgsConstructor
public class FileTypeResolver {

    private final BucketResolver bucketResolver;

    /**
     * Resolves {@link MessageType} from the bucket name.
     * Expects bucket names to follow the convention {@code message-<type>}.
     *
     * @param filename e.g. "photo.jpg"
     * @return MessageType, e.g. IMAGE
     */
    public MessageType resolveMessageType(String filename) {
        String bucket = bucketResolver.resolveBucket(filename);
        // bucket = "message-image" → extract "image" → IMAGE
        String suffix = bucket.substring(bucket.indexOf('-') + 1).toUpperCase();
        return MessageType.valueOf(suffix);
    }
}
