package org.d1ff.messageservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "supported-file-types")
public class FileTypeProperties {
    private List<String> video;
    private List<String> image;
    private List<String> audio;
    private List<String> document;

    public Map<String, String> getExtensionToTypeMap() {
        Map<String, String> map = new HashMap<>();

        if (video != null) {
            video.forEach(ext -> map.put(ext.toLowerCase(), "video"));
        }
        if (image != null) {
            image.forEach(ext -> map.put(ext.toLowerCase(), "image"));
        }
        if (audio != null) {
            audio.forEach(ext -> map.put(ext.toLowerCase(), "audio"));
        }
        if (document != null) {
            document.forEach(ext -> map.put(ext.toLowerCase(), "document"));
        }

        return map;
    }
}

