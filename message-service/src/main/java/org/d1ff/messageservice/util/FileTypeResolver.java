package org.d1ff.messageservice.util;

import lombok.RequiredArgsConstructor;
import org.d1ff.bucket.BucketResolver;
import org.d1ff.messageservice.entity.MessageType;
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
     * Resolves bucket name for the given filename using config from application.yml.
     *
     * @param filename e.g. "photo.jpg"
     * @return bucket name, e.g. "message-image"
     */
    public String resolveBucket(String filename) {
        return bucketResolver.resolveBucket(filename);
    }

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
