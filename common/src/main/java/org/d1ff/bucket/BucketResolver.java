package org.d1ff.bucket;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class BucketResolver {

    private final BucketResolverProperties properties;

    private final Map<String, String> extensionToBucket = new HashMap<>();

    @PostConstruct
    void init() {
        properties.getBuckets().forEach((bucketName, config) -> {
            if (config.getExtensions() == null) return;
            for (String ext : config.getExtensions()) {
                String normalized = ext.trim().toLowerCase();
                String prev = extensionToBucket.put(normalized, bucketName);
                if (prev != null) {
                    log.warn("Extension '{}' is mapped to multiple buckets: '{}' and '{}'. Using '{}'.",
                            normalized, prev, bucketName, bucketName);
                }
            }
        });
        log.info("BucketResolver initialized with {} extension(s) across {} bucket(s)",
                extensionToBucket.size(), properties.getBuckets().size());
    }

    public String resolveBucket(String filename) {
        String ext = extractExtension(filename);
        String bucket = extensionToBucket.get(ext);
        if (bucket == null) {
            throw new IllegalArgumentException(
                    "No bucket configured for extension '" + ext + "' (file: " + filename + ")");
        }
        return bucket;
    }

    public boolean isExtensionAllowed(String filename) {
        try {
            return extensionToBucket.containsKey(extractExtension(filename));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public java.util.Set<String> getBucketNames() {
        return properties.getBuckets().keySet();
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Filename has no extension: " + filename);
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase().trim();
    }
}
