package org.d1ff.messageservice.service.interfaces;

import org.d1ff.messageservice.dto.MinioFileProperties;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    MinioFileProperties uploadFile(MultipartFile file);
    String generatePresignedUrl(MinioFileProperties minioFileProperties);
}
