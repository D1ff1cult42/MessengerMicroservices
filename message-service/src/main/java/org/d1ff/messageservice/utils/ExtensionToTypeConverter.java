package org.d1ff.messageservice.utils;

import lombok.RequiredArgsConstructor;
import org.d1ff.messageservice.config.FileTypeProperties;
import org.d1ff.messageservice.entity.MessageType;
import org.d1ff.messageservice.exceptions.minio.UnknownFileExtensionException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExtensionToTypeConverter {

    private final FileTypeProperties fileTypeProperties;

    public MessageType convert(String filename) {
        String extension = getExtensionByFilename(filename);

        Map<String, String> extensionMap = fileTypeProperties.getExtensionToTypeMap();
        String type = extensionMap.get(extension.toLowerCase());

        if (type == null) {
            throw new UnknownFileExtensionException(extension);
        }

        return MessageType.valueOf(type.toUpperCase());
    }

    private static String getExtensionByFilename(String filename){
        if (filename == null || !filename.contains(".")) {
            throw new UnknownFileExtensionException(filename);
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        return extension;
    }
}
