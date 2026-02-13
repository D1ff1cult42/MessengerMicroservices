package org.d1ff.messageservice.util;

import lombok.RequiredArgsConstructor;
import org.d1ff.bucket.BucketResolver;
import org.d1ff.messageservice.entity.MessageType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileTypeResolver {

    private final BucketResolver bucketResolver;


    public MessageType resolveMessageType(String filename) {
        String bucket = bucketResolver.resolveBucket(filename);
        String suffix = bucket.substring(bucket.indexOf('_') + 1).toUpperCase();
        return MessageType.valueOf(suffix);
    }
}
